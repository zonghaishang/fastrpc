package com.fast.fastrpc.registry;

import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.PrefixThreadFactory;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
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

    private URL url;

    // cached provider file.
    private File file;

    private final Properties properties = new Properties();

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
            throw new IllegalArgumentException("register url == null");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Register: " + url);
        }



//        registered.put(url.getParameter(Constants.CATEGORY_KEY, Constants.PROVIDER), url);
    }

    @Override
    public void unregister(URL url) {
        if (url == null) {
            throw new IllegalArgumentException("unregister url == null");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Unregister: " + url);
        }
        registered.remove(url);
    }

    @Override
    public void subscribe(URL url, RegistryListener listener) {
        if (url == null) {
            throw new IllegalArgumentException("subscribe url == null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("subscribe listener == null");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Subscribe: " + url);
        }
//        Set<RegistryListener> listeners = subscribed.computeIfAbsent(url, n -> new ConcurrentHashSet<>());
//        listeners.add(listener);
    }

    @Override
    public void unsubscribe(URL url, RegistryListener listener) {
        if (url == null) {
            throw new IllegalArgumentException("unsubscribe url == null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("unsubscribe listener == null");
        }
        if (logger.isInfoEnabled()) {
            logger.info("Unsubscribe: " + url);
        }
        Set<RegistryListener> listeners = subscribed.get(url);
        if (listeners != null) {
            listeners.remove(listener);
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
        private long version;

        private SavePropertyTask(long version) {
            this.version = version;
        }

        @Override
        public void run() {
            saveProperties0(version);
        }
    }
}
