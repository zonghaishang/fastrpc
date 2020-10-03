package com.fast.fastrpc.config;

import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.utils.ConfUtils;
import com.fast.fastrpc.common.utils.NetUtils;
import com.fast.fastrpc.common.utils.UrlUtils;

import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yiji
 * @version : AbstractInterfaceConfig.java, v 0.1 2020-10-01
 */
public abstract class AbstractInterfaceConfig extends AbstractMethodConfig {

    // proxy type
    protected String proxy;

    // cluster type
    protected String cluster;

    // filter
    protected String filter;

    // request uniqueId
    protected String uniqueId;

    // service unit
    protected String unit;

    // interface type name
    protected String interfaceName;

    // interface type
    protected Class<?> interfaceClass;

    // application info
    protected ApplicationConfig application;

    // registry centers
    protected List<RegistryConfig> registries;

    // method configuration
    protected List<MethodConfig> methods;

    protected void checkRegistry() {
        if (registries == null || registries.isEmpty()) {
            String address = ConfUtils.getProperty("rpc.registry.address");
            if (address != null && address.length() > 0) {
                registries = new ArrayList<>();
                String[] as = address.split("\\s*[|]+\\s*");
                for (String a : as) {
                    RegistryConfig registryConfig = new RegistryConfig();
                    registryConfig.setAddress(a);
                    registries.add(registryConfig);
                }
            }
        }
        if ((registries == null || registries.isEmpty())) {
            throw new IllegalStateException((getClass().getSimpleName().startsWith("Reference")
                    ? "No such any registry to refer service in consumer "
                    : "No such any registry to export service in provider ")
                    + NetUtils.getLocalHost()
                    + ", Please add <rpc:registry address=\"...\" /> to your spring config. "
                    + "If you want unregister, please set <rpc:service registry=\"disabled\" />");
        }
        for (RegistryConfig registryConfig : registries) {
            injectProperties(registryConfig);
        }
    }

    protected void checkApplication() {
        if (application == null) {
            String applicationName = ConfUtils.getProperty("rpc.application.name");
            if (applicationName != null && applicationName.length() > 0) {
                application = new ApplicationConfig();
            }
        }
        if (application == null) {
            throw new IllegalStateException(
                    "No such application config! Please add <rpc:application name=\"...\" /> to your spring config.");
        }
        injectProperties(application);
    }

    protected List<URL> loadRegistries(boolean provider) {
        checkRegistry();
        List<URL> registryList = new ArrayList<URL>();
        if (registries != null && !registries.isEmpty()) {
            for (RegistryConfig config : registries) {
                String address = config.getAddress();
                if (address.length() > 0 && !Constants.DISABLE_KEY.equalsIgnoreCase(address)) {
                    Map<String, String> map = new HashMap<>();
                    appendParameters(map, application, null);
                    appendParameters(map, config, null);
                    map.put(Constants.INTERFACE_KEY, Registry.class.getName());
                    List<URL> urls = UrlUtils.parseURLs(address, map);
                    for (URL url : urls) {
                        url = url.addParameter(Constants.REGISTRY_PROTOCOL, url.getProtocol());
                        url = url.setProtocol(Constants.REGISTRY_PROTOCOL);
                        if ((provider && url.getParameter(Constants.REGISTER_KEY, true))
                                || (!provider && url.getParameter(Constants.SUBSCRIBE_KEY, true))) {
                            registryList.add(url);
                        }
                    }
                }
            }
        }
        return registryList;
    }

    protected void checkInterfaceAndMethods() {

        if (interfaceName == null || interfaceName.length() == 0) {
            throw new IllegalStateException("rpc interface not allow null!");
        }

        try {
            interfaceClass = Class.forName(interfaceName, true, Thread.currentThread().getContextClassLoader());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException(e.getMessage(), e);
        }

        if (!interfaceClass.isInterface()) {
            throw new IllegalStateException("The interface class " + interfaceClass + " is not a interface!");
        }

        if (methods != null && !methods.isEmpty()) {
            for (MethodConfig methodBean : methods) {
                String methodName = methodBean.getName();
                if (methodName == null || methodName.length() == 0) {
                    throw new IllegalStateException("<rpc:method> name attribute is required!");
                }
                boolean hasMethod = false;
                for (java.lang.reflect.Method method : interfaceClass.getMethods()) {
                    if (method.getName().equals(methodName)) {
                        hasMethod = true;
                        break;
                    }
                }
                if (!hasMethod) {
                    throw new IllegalStateException("The interface " + interfaceClass.getName()
                            + " not found method " + methodName);
                }
            }
        }
    }

    public String getProxy() {
        return proxy;
    }

    public void setProxy(String proxy) {
        this.proxy = proxy;
    }

    public String getCluster() {
        return cluster;
    }

    public void setCluster(String cluster) {
        this.cluster = cluster;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public ApplicationConfig getApplication() {
        return application;
    }

    public void setApplication(ApplicationConfig application) {
        this.application = application;
    }

    public List<RegistryConfig> getRegistries() {
        return registries;
    }

    public void setRegistries(List<RegistryConfig> registries) {
        this.registries = registries;
    }

    public Class<?> getInterfaceClass() {
        return interfaceClass;
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
        setInterface(interfaceClass.getName());
    }

    public String getInterface() {
        return interfaceName;
    }

    public void setInterface(String interfaceName) {
        this.interfaceName = interfaceName;
    }

    public List<MethodConfig> getMethods() {
        return methods;
    }

    public void setMethods(List<MethodConfig> methods) {
        this.methods = methods;
    }
}
