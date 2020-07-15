package com.fast.fastrpc.common.spi;

import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Framework core SPI loading implementation.
 *
 * @author yiji
 * @version : ExtensionLoader.java, v 0.1 2020-07-12
 */
public class ExtensionLoader<T> {

    private static final Logger logger = LoggerFactory.getLogger(ExtensionLoader.class);

    private static final String SERVICES_DIRECTORY = "META-INF/services/fast-rpc";

    private static final ConcurrentMap<Class<?>, ExtensionLoader<?>> EXTENSION_LOADERS = new ConcurrentHashMap<>();

    private static final ConcurrentMap<Class<?>, Object> EXTENSION_OBJECTS = new ConcurrentHashMap<>();

    private final ConcurrentMap<Class<?>, String> cachedNames = new ConcurrentHashMap<>();

    private final Holder<Map<String, Class<?>>> cachedClasses = new Holder<>();

    private final Map<String, Activate> cachedActivates = new ConcurrentHashMap<>();

    private final ConcurrentMap<String, Holder<Object>> cachedObjects = new ConcurrentHashMap<>();

    private final Holder<Object> cachedAdaptiveObject = new Holder<>();

    private Map<String, IllegalStateException> exceptions = new ConcurrentHashMap<>();

    private Class<?> type;

    private ExtensionFactory objectFactory;

    private volatile Class<?> cachedAdaptiveClass;

    private String cachedDefaultName;

    private volatile Throwable adaptiveObjectError;

    private Set<Class<?>> cachedWrapperClasses;

    private ExtensionLoader(Class<?> type) {
        this.type = type;
        if (type != ExtensionFactory.class) {
            this.objectFactory = ExtensionLoader.getExtensionLoader(ExtensionFactory.class).getAdaptiveExtension();
        }
    }

    public static <T> ExtensionLoader<T> getExtensionLoader(Class<T> type) {
        if (type == null)
            throw new IllegalArgumentException("Extension type is required, actual is 'null'.");
        if (!type.isInterface()) {
            throw new IllegalArgumentException("Extension type(" + type + ") is not interface!");
        }
        if (!type.isAnnotationPresent(SPI.class)) {
            throw new IllegalArgumentException("Extension type(" + type +
                    ") is not extension, because without @" + SPI.class.getSimpleName() + " Annotation!");
        }

        ExtensionLoader<T> loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        if (loader == null) {
            EXTENSION_LOADERS.putIfAbsent(type, new ExtensionLoader<T>(type));
            loader = (ExtensionLoader<T>) EXTENSION_LOADERS.get(type);
        }
        return loader;
    }

    public T getExtension(String name) {
        if (name == null || name.length() == 0)
            throw new IllegalArgumentException("Extension name is required, actual is 'null'");
        if ("true".equals(name)) {
            return getDefaultExtension();
        }
        Holder<Object> holder = cachedObjects.get(name);
        if (holder == null) {
            cachedObjects.putIfAbsent(name, new Holder<>());
            holder = cachedObjects.get(name);
        }
        Object instance = holder.get();
        if (instance == null) {
            synchronized (holder) {
                instance = holder.get();
                if (instance == null) {
                    instance = createExtension(name);
                    holder.set(instance);
                }
            }
        }
        return (T) instance;
    }

    public List<T> getActivateExtension(URL url, String key) {
        return getActivateExtension(url, key, null);
    }

    public List<T> getActivateExtension(URL url, String key, String group) {
        String value = url.getParameter(key);
        return getActivateExtension(url, (value == null || value.length() == 0)
                ? new String[0] : Constants.COMMA_SPLIT_PATTERN.split(value), group);
    }

    public T getAdaptiveExtension() {
        Object instance = cachedAdaptiveObject.get();
        if (instance == null) {
            if (adaptiveObjectError == null) {
                synchronized (cachedAdaptiveObject) {
                    instance = cachedAdaptiveObject.get();
                    if (instance == null) {
                        try {
                            instance = createAdaptiveExtension();
                            cachedAdaptiveObject.set(instance);
                        } catch (Throwable t) {
                            adaptiveObjectError = t;
                            throw new IllegalStateException("fail to create adaptive object: " + t.toString(), t);
                        }
                    }
                }
            } else {
                throw new IllegalStateException("fail to create adaptive object: " + adaptiveObjectError.getMessage(), adaptiveObjectError);
            }
        }

        return (T) instance;
    }

    private T createAdaptiveExtension() {
        try {
            Class<?> adaptiveImpl = getAdaptiveExtensionClass();
            if (adaptiveImpl == null) throw new IllegalStateException("No adaptive extension found, type : + " + type);

            return injectExtension((T) adaptiveImpl.newInstance());
        } catch (Exception e) {
            throw new IllegalStateException("Can not create adaptive extension " + type + ", cause: " + e.getMessage(), e);
        }
    }

    private Class<?> getAdaptiveExtensionClass() {
        getExtensionClasses();
        return cachedAdaptiveClass;
    }

    private T injectExtension(T instance) {
        try {
            if (objectFactory != null) {
                for (Method method : instance.getClass().getMethods()) {
                    if (isWrapperMethod(method)) {
                        Class<?> injectType = method.getParameterTypes()[0];
                        try {
                            String property = method.getName().length() > 3
                                    ? method.getName().substring(3, 4).toLowerCase() + method.getName().substring(4) : "";
                            if (isInjectExtension(injectType)) {
                                Object object = objectFactory.getExtension(injectType, property);
                                if (object != null) {
                                    method.invoke(instance, object);
                                }
                            }
                        } catch (Exception e) {
                            logger.error("fail to inject via method " + method.getName()
                                    + " of interface " + type.getName() + ": " + e.getMessage(), e);
                        }
                    }
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return instance;
    }

    private boolean isInjectExtension(Class<?> injectType) {
        return injectType.isInterface() && injectType.isAnnotationPresent(SPI.class);
    }

    private Map<String, Class<?>> getExtensionClasses() {
        Map<String, Class<?>> classes = cachedClasses.get();
        if (classes == null) {
            synchronized (cachedClasses) {
                classes = cachedClasses.get();
                if (classes == null) {
                    classes = loadExtensionClasses();
                    cachedClasses.set(classes);
                }
            }
        }
        return classes;
    }

    private Map<String, Class<?>> loadExtensionClasses() {
        final SPI defaultAnnotation = type.getAnnotation(SPI.class);
        if (defaultAnnotation != null) {
            String value = defaultAnnotation.value();
            if ((value = value.trim()).length() > 0) {
                cachedDefaultName = value;
            }
        }

        Map<String, Class<?>> extensionClasses = new HashMap<String, Class<?>>();
        loadDirectory(extensionClasses, SERVICES_DIRECTORY);
        return extensionClasses;
    }

    private void loadDirectory(Map<String, Class<?>> extensionClasses, String dir) {
        String fileName = dir + type.getName();
        try {
            Enumeration<java.net.URL> urls;
            ClassLoader classLoader = findClassLoader();
            if (classLoader != null) {
                urls = classLoader.getResources(fileName);
            } else {
                urls = ClassLoader.getSystemResources(fileName);
            }
            if (urls != null) {
                while (urls.hasMoreElements()) {
                    loadResource(extensionClasses, classLoader, urls.nextElement());
                }
            }
        } catch (Throwable t) {
            logger.error("Exception when load extension : " +
                    type + ", description file: " + fileName + ".", t);
        }
    }

    private void loadResource(Map<String, Class<?>> extensionClasses, ClassLoader classLoader, java.net.URL resource) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(resource.openStream(), "utf-8"));
            try {
                String line;
                while ((line = reader.readLine()) != null) {
                    final int ci = line.indexOf('#');
                    if (ci >= 0) line = line.substring(0, ci);
                    line = line.trim();
                    if (line.length() > 0) {
                        try {
                            String name = null;
                            int i = line.indexOf('=');
                            if (i > 0) {
                                name = line.substring(0, i).trim();
                                line = line.substring(i + 1).trim();
                            }
                            if (line.length() > 0) {
                                loadClass(extensionClasses, resource, Class.forName(line, true, classLoader), name);
                            }
                        } catch (Throwable t) {
                            IllegalStateException e;
                            if (t instanceof IllegalStateException) {
                                e = (IllegalStateException) (t);
                            } else {
                                e = new IllegalStateException("Failed to load extension: " + type + ", class line: " + line + " in " + resource + ", cause: " + t.getMessage(), t);
                            }
                            exceptions.put(line, e);
                        }
                    }
                }
            } finally {
                reader.close();
            }
        } catch (Throwable t) {
            logger.error("Exception when load extension : " + type + " in " + resource, t);
        }
    }

    private void loadClass(Map<String, Class<?>> extensionClasses, java.net.URL resourceURL, Class<?> impl, String name) throws NoSuchMethodException {
        if (!type.isAssignableFrom(impl)) {
            throw new IllegalStateException("Error when load extension : " + type + ", class " + impl.getName() + "is not subtype of interface.");
        }
        if (impl.isAnnotationPresent(Adaptive.class)) {
            if (cachedAdaptiveClass == null) {
                cachedAdaptiveClass = impl;
            } else if (!cachedAdaptiveClass.equals(impl)) {
                throw new IllegalStateException("More than 1 adaptive class found: "
                        + cachedAdaptiveClass.getClass().getName()
                        + ", " + impl.getClass().getName());
            }
        } else if (isWrapperClass(impl)) {
            Set<Class<?>> wrappers = cachedWrapperClasses;
            if (wrappers == null) {
                cachedWrapperClasses = Collections.synchronizedSet(new HashSet<Class<?>>());
                wrappers = cachedWrapperClasses;
            }
            wrappers.add(impl);
        } else {
            if (name == null || name.length() == 0) {
                name = findExtensionName(impl);
                if (name.length() == 0) {
                    throw new IllegalStateException("No such extension name for the class " + impl.getName() + " in the config " + resourceURL);
                }
            }
            String[] names = Constants.COMMA_SPLIT_PATTERN.split(name);
            if (names != null && names.length > 0) {
                Activate activate = impl.getAnnotation(Activate.class);
                if (activate != null) {
                    cachedActivates.put(names[0], activate);
                }
                for (String n : names) {
                    if (!cachedNames.containsKey(impl)) {
                        cachedNames.put(impl, n);
                    }
                    Class<?> c = extensionClasses.get(n);
                    if (c == null) {
                        extensionClasses.put(n, impl);
                    } else if (c != impl) {
                        throw new IllegalStateException("Duplicate extension " + type.getName() + " name " + n + " on " + c.getName() + " and " + impl.getName());
                    }
                }
            }
        }
    }

    public List<T> getActivateExtension(URL url, String[] values, String group) {
        List<T> filtered = new ArrayList<T>();
        List<String> names = values == null ? new ArrayList<String>() : Arrays.asList(values);
        if (!names.contains(Constants.REMOVE_VALUE_PREFIX + Constants.DEFAULT_KEY)) {
            getExtensionClasses();
            for (Map.Entry<String, Activate> entry : cachedActivates.entrySet()) {
                String name = entry.getKey();
                Activate activate = entry.getValue();
                if (isMatchGroup(group, activate.group())) {
                    T ext = getExtension(name);
                    if (!names.contains(name)
                            && !names.contains(Constants.REMOVE_VALUE_PREFIX + name)
                            && isActive(activate, url)) {
                        filtered.add(ext);
                    }
                }
            }
        }

        List<T> specified = new ArrayList<>();
        for (int i = 0; i < names.size(); i++) {
            String name = names.get(i);
            if (!name.startsWith(Constants.REMOVE_VALUE_PREFIX)
                    && !names.contains(Constants.REMOVE_VALUE_PREFIX + name)) {
                T ext = getExtension(name);
                specified.add(ext);
            }
        }
        if (!specified.isEmpty()) {
            filtered.addAll(specified);
        }

        Collections.sort(filtered, ActivateComparator.COMPARATOR);

        return filtered;
    }

    private boolean isMatchGroup(String group, String[] groups) {
        if (group == null || group.length() == 0) {
            return true;
        }
        if (groups != null && groups.length > 0) {
            for (String g : groups) {
                if (group.equals(g)) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean isActive(Activate activate, URL url) {
        String[] keys = activate.value();
        if (keys.length == 0) {
            return true;
        }
        for (String key : keys) {
            for (Map.Entry<String, String> entry : url.getParameters().entrySet()) {
                String k = entry.getKey();
                String v = entry.getValue();
                if ((k.equals(key) || k.endsWith("." + key))
                        && (v != null && v.length() > 0)) {
                    return true;
                }
            }
        }
        return false;
    }

    private T createExtension(String name) {
        Class<?> clazz = getExtensionClasses().get(name);
        if (clazz == null) {
            throw findException(name);
        }
        try {
            T instance = (T) EXTENSION_OBJECTS.get(clazz);
            if (instance == null) {
                EXTENSION_OBJECTS.putIfAbsent(clazz, clazz.newInstance());
                instance = (T) EXTENSION_OBJECTS.get(clazz);
            }
            injectExtension(instance);
            Set<Class<?>> wrapperClasses = cachedWrapperClasses;
            if (wrapperClasses != null && !wrapperClasses.isEmpty()) {
                for (Class<?> wrapperClass : wrapperClasses) {
                    instance = injectExtension((T) wrapperClass.getConstructor(type).newInstance(instance));
                }
            }
            return instance;
        } catch (Throwable t) {
            throw new IllegalStateException("Extension instance(name: " + name + ", class: " +
                    type + ")  could not be instantiated: " + t.getMessage(), t);
        }
    }

    private IllegalStateException findException(String name) {
        for (Map.Entry<String, IllegalStateException> entry : exceptions.entrySet()) {
            if (entry.getKey().toLowerCase().contains(name.toLowerCase())) {
                return entry.getValue();
            }
        }
        StringBuilder buf = new StringBuilder("No such extension " + type.getName() + " by name " + name);

        int i = 1;
        for (Map.Entry<String, IllegalStateException> entry : exceptions.entrySet()) {
            if (i == 1) {
                buf.append(", possible causes: ");
            }

            buf.append("\r\n(");
            buf.append(i++);
            buf.append(") ");
            buf.append(entry.getKey());
            buf.append(":\r\n");
            buf.append(entry.getValue());
        }
        return new IllegalStateException(buf.toString());
    }

    private boolean isWrapperClass(Class<?> clazz) {
        try {
            clazz.getConstructor(type);
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    private boolean isWrapperMethod(Method method) {
        return method.getName().startsWith("set")
                && method.getParameterTypes().length == 1
                && Modifier.isPublic(method.getModifiers());
    }

    protected ClassLoader findClassLoader() {
        return ExtensionLoader.class.getClassLoader();
    }

    private String findExtensionName(Class<?> clazz) {
        String name = clazz.getSimpleName();
        if (name.endsWith(type.getSimpleName())) {
            name = name.substring(0, name.length() - type.getSimpleName().length());
        }
        return name.toLowerCase();
    }

    public Set<String> getSupportedExtensions() {
        return Collections.unmodifiableSet(new TreeSet<>(getExtensionClasses().keySet()));
    }

    public T getDefaultExtension() {
        getExtensionClasses();
        if (null == cachedDefaultName || cachedDefaultName.length() == 0
                || "true".equals(cachedDefaultName)) {
            return null;
        }
        return getExtension(cachedDefaultName);
    }

    private static class Holder<T> {

        private volatile T value;

        public void set(T value) {
            this.value = value;
        }

        public T get() {
            return value;
        }

    }

}