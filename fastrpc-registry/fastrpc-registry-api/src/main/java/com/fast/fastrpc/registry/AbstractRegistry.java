package com.fast.fastrpc.registry;

import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.PrefixThreadFactory;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.common.utils.UrlUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yiji
 * @version : AbstractRegistry.java, v 0.1 2020-09-02
 */
public abstract class AbstractRegistry implements Registry {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private static final String separator = File.separator;
    private static final String cachedPrefix = "registry-";
    private static final String cacheDirectory = System.getProperty("user.home") + separator + ".fastrpc";
    private static final String urlSplit = "\\s+";

    private final URL url;

    // cached provider file.
    private File file;

    private final Properties properties = new Properties();

    private final Set<URL> online = new CopyOnWriteArraySet<>();

    private final ConcurrentMap<String, List<URL>> registered = new ConcurrentHashMap<>();

    private final ConcurrentMap<URL, Set<RegistryListener>> subscribed = new ConcurrentHashMap<>();

    private final ConcurrentMap<URL, Map<String, List<URL>>> notified = new ConcurrentHashMap<>();

    private final AtomicLong cachedVersion = new AtomicLong();

    private static final int MAX_RETRY_TIMES = 3;

    private final AtomicInteger retriedTimes = new AtomicInteger();

    private static final ExecutorService registryCacheExecutor = Executors.newFixedThreadPool(1, new PrefixThreadFactory("AsyncSaveRegistryCache"));

    public AbstractRegistry(URL url) {
        this.url = url;
        createCacheFileIfRequired();
        loadProperties();
    }

    @Override
    public void register(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("register url is required");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Register: " + url);
        }

        String category = url.getParameter(Constants.CATEGORY_KEY, Constants.PROVIDERS);
        List<URL> urls = registered.get(category);
        if (urls == null) {
            synchronized (registered) {
                urls = new CopyOnWriteArrayList<>();
                registered.put(category, urls);
            }
        }
        urls.add(url);
    }

    @Override
    public void unregister(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("unregister url is required");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Unregister: " + url);
        }

        String category = url.getParameter(Constants.CATEGORY_KEY, Constants.PROVIDERS);
        List<URL> urls = registered.get(category);
        if (urls == null || urls.isEmpty()) {
            return;
        }
        urls.remove(url);
    }

    @Override
    public void subscribe(URL url, RegistryListener listener) {
        if (url == null) {
            throw new IllegalArgumentException("subscribe url is required");
        }
        if (listener == null) {
            throw new IllegalArgumentException("subscribe listener is required");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Subscribe: " + url);
        }
        Set<RegistryListener> listeners = subscribed.get(url);
        if (listeners == null) {
            synchronized (subscribed) {
                subscribed.put(url, listeners = new CopyOnWriteArraySet<>());
            }
        }
        listeners.add(listener);
    }

    @Override
    public void unsubscribe(URL url, RegistryListener listener) {
        if (url == null) {
            throw new IllegalArgumentException("unsubscribe url is required");
        }
        if (listener == null) {
            throw new IllegalArgumentException("unsubscribe listener is required");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Unsubscribe: " + url);
        }
        Set<RegistryListener> listeners = subscribed.get(url);
        if (listeners != null) {
            listeners.remove(listener);
        }
    }

    @Override
    public void online(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("online url is required");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Online: " + url);
        }
        this.online.add(url);
    }

    @Override
    public void online() {
        // convert registered urls to application level
        String category = url.getParameter(Constants.CATEGORY_KEY, Constants.PROVIDERS);
        List<URL> urls = registered.get(category);
        if (urls == null || urls.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("No exported service to online: " + url);
            }
            return;
        }

        List<URL> exportedUrls = getExportedUrls(urls);

        for (URL url : getOnline()) {
            online(url);
        }
    }

    @Override
    public void offline(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("offline url is required");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Offline: " + url);
        }
        this.online.remove(url);
    }

    @Override
    public void offline() {
        for (URL url : getOnline()) {
            offline(url);
        }
    }

    protected List<URL> getExportedUrls(List<URL> urls) {
        // TODO need to be impl.
        return null;
    }

    protected void recover() throws Exception {
        // register
        Set<URL> recoverOnline = new HashSet<>(getOnline());
        if (!recoverOnline.isEmpty()) {
            for (URL url : recoverOnline) {
                if (logger.isInfoEnabled()) {
                    logger.info("Recover online url " + url);
                }
                online(url);
            }
        }

        // subscribe
        Map<URL, Set<RegistryListener>> recoverSubscribed = new HashMap<>(getSubscribed());
        if (!recoverSubscribed.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("Recover subscribe url " + recoverSubscribed.keySet());
            }
            for (Map.Entry<URL, Set<RegistryListener>> entry : recoverSubscribed.entrySet()) {
                URL url = entry.getKey();
                for (RegistryListener listener : entry.getValue()) {
                    subscribe(url, listener);
                }
            }
        }
    }

    protected void notify(URL url, RegistryListener listener, List<URL> urls) {
        if (url == null) {
            throw new IllegalArgumentException("notify url is required");
        }
        if (listener == null) {
            throw new IllegalArgumentException("notify listener is required");
        }
        if ((urls == null || urls.isEmpty())) {
            logger.warn("Ignore empty notify urls for subscribe url " + url);
            return;
        }
        if (logger.isInfoEnabled()) {
            logger.info("Notify urls for subscribe url " + url + ", urls: " + urls);
        }
        Map<String, List<URL>> result = new HashMap<>();
        for (URL u : urls) {
            if (UrlUtils.isMatch(url, u)) {
                String category = u.getParameter(Constants.CATEGORY_KEY, Constants.PROVIDERS);
                List<URL> categoryList = result.get(category);
                if (categoryList == null) {
                    categoryList = new ArrayList<>();
                    result.put(category, categoryList);
                }
                categoryList.add(u);
            }
        }
        if (result.isEmpty()) {
            return;
        }
        Map<String, List<URL>> categoryNotified = notified.get(url);
        if (categoryNotified == null) {
            notified.putIfAbsent(url, new ConcurrentHashMap<String, List<URL>>());
            categoryNotified = notified.get(url);
        }
        for (Map.Entry<String, List<URL>> entry : result.entrySet()) {
            String category = entry.getKey();
            List<URL> categoryList = entry.getValue();
            categoryNotified.put(category, categoryList);
            saveProperties(url);
            listener.notify(categoryList);
        }
    }

    @Override
    public void destroy() {
        if (logger.isInfoEnabled()) {
            logger.info("Destroy registry:" + getUrl());
        }

        // remove registered providers.
        offline();

        Map<String, List<URL>> destroyRegistered = new HashMap<>(getRegistered());
        if (!destroyRegistered.isEmpty()) {
            for (Map.Entry<String, List<URL>> entry : destroyRegistered.entrySet()) {
                for (URL url : new HashSet<>(entry.getValue())) {
                    if (url.getParameter(Constants.DYNAMIC_KEY, true)) {
                        try {
                            unregister(url);
                            if (logger.isInfoEnabled()) {
                                logger.info("Destroy unregister url " + url);
                            }
                        } catch (Throwable t) {
                            logger.warn("Failed to unregister url " + url + " to registry " + getUrl() + " on destroy, cause: " + t.getMessage(), t);
                        }
                    }
                }
            }
        }
        Map<URL, Set<RegistryListener>> destroySubscribed = new HashMap<>(getSubscribed());
        if (!destroySubscribed.isEmpty()) {
            for (Map.Entry<URL, Set<RegistryListener>> entry : destroySubscribed.entrySet()) {
                URL url = entry.getKey();
                for (RegistryListener listener : entry.getValue()) {
                    try {
                        unsubscribe(url, listener);
                        if (logger.isInfoEnabled()) {
                            logger.info("Destroy unsubscribe url " + url);
                        }
                    } catch (Throwable t) {
                        logger.warn("Failed to unsubscribe url " + url + " to registry " + getUrl() + " on destroy, cause: " + t.getMessage(), t);
                    }
                }
            }
        }
    }

    private void loadProperties() {
        if (file != null && file.exists()) {
            InputStream in = null;
            try {
                in = new FileInputStream(file);
                properties.load(in);
                if (logger.isInfoEnabled()) {
                    logger.info("Load registry cache file " + file + ", data: " + properties);
                }
            } catch (Throwable e) {
                logger.warn("Failed to load registry cache file " + file, e);
            } finally {
                if (in != null) {
                    try {
                        in.close();
                    } catch (IOException e) {
                        logger.warn(e.getMessage(), e);
                    }
                }
            }
        }
    }

    protected void saveProperties(URL url) {

        if (file == null) {
            return;
        }

        try {
            StringBuilder buf = new StringBuilder();
            Map<String, List<URL>> categoryNotified = notified.get(url);
            if (categoryNotified != null) {
                for (List<URL> urls : categoryNotified.values()) {
                    for (URL u : urls) {
                        if (buf.length() > 0) {
                            buf.append(urlSplit);
                        }
                        buf.append(u.toFullString());
                    }
                }
            }
            properties.setProperty(url.getServiceKey(), buf.toString());
            long version = cachedVersion.incrementAndGet();
            registryCacheExecutor.execute(new SavePropertyTask(version));
        } catch (Throwable t) {
            logger.warn(t.getMessage(), t);
        }
    }

    protected List<URL> getCachedUrls(URL url) {
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (key != null && key.length() > 0 && key.equals(url.getServiceKey())
                    && value != null && value.length() > 0) {
                String[] arr = value.trim().split(urlSplit);
                List<URL> urls = new ArrayList<>();
                for (String u : arr) {
                    urls.add(URL.valueOf(u));
                }
                return urls;
            }
        }
        return null;
    }

    protected void createCacheFileIfRequired() {
        String cacheFile = url.getParameter(Constants.CACHE_PATH, getCachedFile());
        // disable cache registry.
        if (cacheFile.equals(Constants.DISABLE_KEY)) {
            return;
        }

        this.file = new File(cacheFile);
        try {
            if (!file.exists() && file.getParentFile() != null && !file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    throw new IllegalArgumentException("Failed to create directory" + file.getParentFile() + ", invalid cache file " + file + " or no permission ?");
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to create directory " + file.getParentFile(), e);
            this.file = null;
        }
    }

    protected String getCachedFile() {
        StringBuffer buffer = new StringBuffer();
        String application = this.url.getParameter(Constants.APPLICATION_KEY);
        buffer.append(cacheDirectory).append(separator)
                .append(application).append(separator)
                .append(cachedPrefix).append(getRegistryHost()).append(".cache");
        return buffer.toString();
    }

    protected String getRegistryHost() {
        return this.url.getAddress().replaceAll(":", "-");
    }

    private void saveProperties0(long version) {

        if (file == null || version < cachedVersion.get()) {
            return;
        }

        try {
            File lockfile = new File(file.getAbsolutePath() + ".lock");
            if (!lockfile.exists()) {
                lockfile.createNewFile();
            }

            try (RandomAccessFile accessFile = new RandomAccessFile(lockfile, "rw");
                 FileChannel channel = accessFile.getChannel()) {
                FileLock lock = channel.tryLock();
                if (lock == null) {
                    throw new IOException("Can not lock the registry cache file " + file.getAbsolutePath() + ", ignore and retry later, maybe multi java process use the file, please config: rpc.registry.cacheFile=xxx.properties");
                }

                try {
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    //New data found to be updated
                    if (version < cachedVersion.get()) {
                        retriedTimes.set(0);
                        return;
                    }

                    try (FileOutputStream outputFile = new FileOutputStream(file)) {
                        properties.store(outputFile, "rpc registry cache");
                    }
                } finally {
                    lock.release();
                }
            }
        } catch (Throwable e) {
            retriedTimes.incrementAndGet();
            if (retriedTimes.get() >= MAX_RETRY_TIMES) {
                logger.warn("Failed to save registry cache file after retrying " + MAX_RETRY_TIMES + " times, cause: " + e.getMessage(), e);
                retriedTimes.set(0);
                return;
            }
            if (version < cachedVersion.get()) {
                retriedTimes.set(0);
                return;
            } else {
                registryCacheExecutor.execute(new SavePropertyTask(version));
            }
            logger.warn("Failed to save registry cache file and will retry later, cause: " + e.getMessage(), e);
        }
    }

    private class SavePropertyTask implements Runnable {
        private final long version;

        private SavePropertyTask(long version) {
            this.version = version;
        }

        @Override
        public void run() {
            saveProperties0(version);
        }
    }

    public ConcurrentMap<String, List<URL>> getRegistered() {
        return registered;
    }

    public ConcurrentMap<URL, Set<RegistryListener>> getSubscribed() {
        return subscribed;
    }

    public ConcurrentMap<URL, Map<String, List<URL>>> getNotified() {
        return notified;
    }

    public Set<URL> getOnline() {
        return online;
    }

    @Override
    public URL getUrl() {
        return url;
    }
}
