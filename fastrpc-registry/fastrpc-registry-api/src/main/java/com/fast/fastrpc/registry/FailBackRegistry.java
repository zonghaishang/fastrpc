package com.fast.fastrpc.registry;

import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.PrefixThreadFactory;
import com.fast.fastrpc.common.URL;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

/**
 * @author yiji
 * @version : FailoverRegistry.java, v 0.1 2020-09-18
 */
public abstract class FailBackRegistry extends AbstractRegistry {

    private final ConcurrentMap<URL, Set<RegistryListener>> failedSubscribed = new ConcurrentHashMap<>();

    private final ConcurrentMap<URL, Set<RegistryListener>> failedUnsubscribed = new ConcurrentHashMap<>();

    private final Set<URL> failedOnline = new CopyOnWriteArraySet<>();

    private final Set<URL> failedOffline = new CopyOnWriteArraySet<>();

    private final ConcurrentMap<URL, Map<RegistryListener, List<URL>>> failedNotified = new ConcurrentHashMap<>();

    private final ScheduledExecutorService retryExecutor = Executors.newScheduledThreadPool(1, new PrefixThreadFactory("RegistryRetryTimer", true));

    private final ScheduledFuture<?> retryFuture;

    private final int retryPeriod;

    public FailBackRegistry(URL url) {
        super(url);
        this.retryPeriod = url.getParameter(Constants.REGISTRY_RETRY_PERIOD_KEY, 5000);
        this.retryFuture = retryExecutor.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                try {
                    retry();
                } catch (Throwable t) {
                    logger.error("Unexpected error occurred at failed retry, cause: " + t.getMessage(), t);
                }
            }
        }, retryPeriod, retryPeriod, TimeUnit.MILLISECONDS);
    }

    @Override
    public void online(URL url) {
        super.online(url);
        failedOnline.remove(url);
        failedOffline.remove(url);
        try {
            doOnline(url);
        } catch (Exception e) {
            Throwable t = e;
            boolean check = getUrl().getParameter(Constants.CHECK_KEY, true)
                    && url.getParameter(Constants.CHECK_KEY, true);
            if (check) {
                throw new IllegalStateException("Failed to online " + url + " to registry " + getUrl().getAddress() + ", cause: " + t.getMessage(), t);
            } else {
                logger.error("Failed to online " + url + ", waiting for retry, cause: " + t.getMessage(), t);
            }
            failedOnline.add(url);
        }
    }

    @Override
    public void offline(URL url) {
        super.offline(url);
        failedOnline.remove(url);
        failedOffline.remove(url);
        try {
            doOffline(url);
        } catch (Exception e) {
            Throwable t = e;
            boolean check = getUrl().getParameter(Constants.CHECK_KEY, true)
                    && url.getParameter(Constants.CHECK_KEY, true);
            if (check) {
                throw new IllegalStateException("Failed to offline " + url + " to registry " + getUrl().getAddress() + ", cause: " + t.getMessage(), t);
            } else {
                logger.error("Failed to offline " + url + ", waiting for retry, cause: " + t.getMessage(), t);
            }
            failedOffline.add(url);
        }
    }

    @Override
    public void subscribe(URL url, RegistryListener listener) {
        super.subscribe(url, listener);
        removeFailedSubscribed(url, listener);
        try {
            // Sending a subscription request to the server side
            doSubscribe(url, listener);
        } catch (Exception e) {
            Throwable t = e;

            List<URL> urls = getCachedUrls(url);
            if (urls != null && !urls.isEmpty()) {
                notify(url, listener, urls);
                logger.error("Failed to subscribe " + url + ", Using cached list: " + urls + " from cache file: " + getUrl().getParameter(Constants.CACHE_PATH, getCachedFile()) + ", cause: " + t.getMessage(), t);
            } else {
                boolean check = getUrl().getParameter(Constants.CHECK_KEY, true)
                        && url.getParameter(Constants.CHECK_KEY, true);
                if (check) {
                    throw new IllegalStateException("Failed to subscribe " + url + ", cause: " + t.getMessage(), t);
                } else {
                    logger.error("Failed to subscribe " + url + ", waiting for retry, cause: " + t.getMessage(), t);
                }
            }
            addFailedSubscribed(url, listener);
        }
    }

    @Override
    public void unsubscribe(URL url, RegistryListener listener) {
        super.unsubscribe(url, listener);
        removeFailedSubscribed(url, listener);
        try {
            // Sending a canceling subscription request to the server side
            doUnsubscribe(url, listener);
        } catch (Exception e) {
            Throwable t = e;

            // If the startup detection is opened, the Exception is thrown directly.
            boolean check = getUrl().getParameter(Constants.CHECK_KEY, true)
                    && url.getParameter(Constants.CHECK_KEY, true);
            if (check) {
                throw new IllegalStateException("Failed to unsubscribe " + url + " to registry " + getUrl().getAddress() + ", cause: " + t.getMessage(), t);
            } else {
                logger.error("Failed to unsubscribe " + url + ", waiting for retry, cause: " + t.getMessage(), t);
            }

            // Record a failed registration request to a failed list, retry regularly
            Set<RegistryListener> listeners = failedUnsubscribed.get(url);
            if (listeners == null) {
                failedUnsubscribed.putIfAbsent(url, new CopyOnWriteArraySet<RegistryListener>());
                listeners = failedUnsubscribed.get(url);
            }
            listeners.add(listener);
        }
    }

    @Override
    protected void notify(URL url, RegistryListener listener, List<URL> urls) {
        if (url == null) {
            throw new IllegalArgumentException("notify url == null");
        }
        if (listener == null) {
            throw new IllegalArgumentException("notify listener == null");
        }
        try {
            doNotify(url, listener, urls);
        } catch (Exception t) {
            // Record a failed registration request to a failed list, retry regularly
            Map<RegistryListener, List<URL>> listeners = failedNotified.get(url);
            if (listeners == null) {
                failedNotified.putIfAbsent(url, new ConcurrentHashMap<RegistryListener, List<URL>>());
                listeners = failedNotified.get(url);
            }
            listeners.put(listener, urls);
            logger.error("Failed to notify for subscribe " + url + ", waiting for retry, cause: " + t.getMessage(), t);
        }
    }

    protected void doNotify(URL url, RegistryListener listener, List<URL> urls) {
        super.notify(url, listener, urls);
    }

    protected void retry() {
        retryFailedOnline();
        retryFailedOffline();
        retryFailedSubscribe();
        retryFailedUnsubscribe();
        retryFailedNotify();
    }

    protected void retryFailedNotify() {
        if (!failedNotified.isEmpty()) {
            Iterator<Map.Entry<URL, Map<RegistryListener, List<URL>>>> failed = failedNotified.entrySet().iterator();
            if (logger.isInfoEnabled()) {
                logger.info("Retry notify " + failed);
            }
            try {
                while (failed.hasNext()) {
                    Map.Entry<URL, Map<RegistryListener, List<URL>>> entry = failed.next();
                    Map<RegistryListener, List<URL>> listeners = entry.getValue();
                    if (listeners == null || listeners.isEmpty()) {
                        failed.remove();
                        continue;
                    }

                    Iterator<Map.Entry<RegistryListener, List<URL>>> failedListener = listeners.entrySet().iterator();
                    while (failedListener.hasNext()) {
                        try {
                            Map.Entry<RegistryListener, List<URL>> listenerEntry = failedListener.next();
                            RegistryListener listener = listenerEntry.getKey();
                            List<URL> urls = listenerEntry.getValue();
                            listener.notify(urls);
                            failedListener.remove();
                        } catch (Throwable t) {
                            logger.warn("Failed to retry notify " + failedNotified + ", waiting for again, cause: " + t.getMessage(), t);
                        }
                    }
                }
            } catch (Throwable t) {
                logger.warn("Failed to retry notify " + failedNotified + ", waiting for again, cause: " + t.getMessage(), t);
            }
        }
    }

    protected void retryFailedUnsubscribe() {
        if (!failedUnsubscribed.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("Retry unsubscribe " + failedUnsubscribed);
            }
            try {
                Iterator<Map.Entry<URL, Set<RegistryListener>>> failed = failedUnsubscribed.entrySet().iterator();
                while (failed.hasNext()) {
                    Map.Entry<URL, Set<RegistryListener>> entry = failed.next();
                    URL url = entry.getKey();
                    Set<RegistryListener> listeners = entry.getValue();
                    if (listeners == null || listeners.isEmpty()) {
                        failed.remove();
                        continue;
                    }
                    Iterator<RegistryListener> failedListener = listeners.iterator();
                    while (failedListener.hasNext()) {
                        try {
                            RegistryListener listener = failedListener.next();
                            doUnsubscribe(url, listener);
                            failedListener.remove();
                        } catch (Throwable t) {
                            logger.warn("Failed to retry unsubscribe " + failedUnsubscribed + ", waiting for again, cause: " + t.getMessage(), t);
                        }
                    }
                }
            } catch (Throwable t) {
                logger.warn("Failed to retry unsubscribe " + failedUnsubscribed + ", waiting for again, cause: " + t.getMessage(), t);
            }
        }
    }

    protected void retryFailedSubscribe() {
        if (!failedSubscribed.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("Retry subscribe " + failedSubscribed);
            }
            try {
                Iterator<Map.Entry<URL, Set<RegistryListener>>> failed = failedSubscribed.entrySet().iterator();
                while (failed.hasNext()) {
                    Map.Entry<URL, Set<RegistryListener>> entry = failed.next();
                    URL url = entry.getKey();
                    Set<RegistryListener> listeners = entry.getValue();
                    if (listeners == null || listeners.isEmpty()) {
                        failed.remove();
                        continue;
                    }
                    Iterator<RegistryListener> failedListener = listeners.iterator();
                    while (failedListener.hasNext()) {
                        try {
                            RegistryListener listener = failedListener.next();
                            doSubscribe(url, listener);
                            failedListener.remove();
                        } catch (Throwable t) {
                            logger.warn("Failed to retry subscribe " + failedSubscribed + ", waiting for again, cause: " + t.getMessage(), t);
                        }
                    }
                }
            } catch (Throwable t) {
                logger.warn("Failed to retry subscribe " + failedSubscribed + ", waiting for again, cause: " + t.getMessage(), t);
            }
        }
    }

    protected void retryFailedOffline() {
        if (!failedOffline.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("Retry offline " + failedOffline);
            }
            try {
                URL url = null;
                Iterator<URL> failed = failedOffline.iterator();
                while (failed.hasNext()) {
                    try {
                        url = failed.next();
                        doOffline(url);
                        failed.remove();
                    } catch (Throwable t) {
                        logger.warn("Failed to retry offline url " + url + ", waiting for again, cause: " + t.getMessage(), t);
                    }
                }
            } catch (Throwable t) {
                logger.warn("Failed to retry offline " + failedOffline + ", waiting for again, cause: " + t.getMessage(), t);
            }
        }
    }

    protected void retryFailedOnline() {
        if (!failedOnline.isEmpty()) {
            if (logger.isInfoEnabled()) {
                logger.info("Retry online " + failedOnline);
            }
            try {
                URL url = null;
                Iterator<URL> failed = failedOnline.iterator();
                while (failed.hasNext()) {
                    try {
                        url = failed.next();
                        doOnline(url);
                        failed.remove();
                    } catch (Throwable t) {
                        logger.warn("Failed to retry online url " + url + ", waiting for again, cause: " + t.getMessage(), t);
                    }
                }
            } catch (Throwable t) {
                logger.warn("Failed to retry online " + failedOnline + ", waiting for again, cause: " + t.getMessage(), t);
            }
        }
    }

    protected void addFailedSubscribed(URL url, RegistryListener listener) {
        Set<RegistryListener> listeners = failedSubscribed.get(url);
        if (listeners == null) {
            failedSubscribed.putIfAbsent(url, new CopyOnWriteArraySet<RegistryListener>());
            listeners = failedSubscribed.get(url);
        }
        listeners.add(listener);
    }

    protected void removeFailedSubscribed(URL url, RegistryListener listener) {
        Set<RegistryListener> listeners = failedSubscribed.get(url);
        if (listeners != null) {
            listeners.remove(listener);
        }
        listeners = failedUnsubscribed.get(url);
        if (listeners != null) {
            listeners.remove(listener);
        }
        Map<RegistryListener, List<URL>> notified = failedNotified.get(url);
        if (notified != null) {
            notified.remove(listener);
        }
    }

    protected abstract void doOnline(URL url);

    protected abstract void doOffline(URL url);

    protected abstract void doSubscribe(URL url, RegistryListener listener);

    protected abstract void doUnsubscribe(URL url, RegistryListener listener);

}
