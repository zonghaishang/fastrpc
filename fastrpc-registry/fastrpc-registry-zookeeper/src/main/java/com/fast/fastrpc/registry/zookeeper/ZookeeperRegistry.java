package com.fast.fastrpc.registry.zookeeper;

import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.utils.UrlUtils;
import com.fast.fastrpc.registry.FailBackRegistry;
import com.fast.fastrpc.registry.RegistryListener;
import com.fast.fastrpc.remoting.zookeeper.ChildListener;
import com.fast.fastrpc.remoting.zookeeper.StateListener;
import com.fast.fastrpc.remoting.zookeeper.ZookeeperClient;
import com.fast.fastrpc.remoting.zookeeper.ZookeeperTransporter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author yiji
 * @version : ZookeeperRegistry.java, v 0.1 2020-09-30
 */
public class ZookeeperRegistry extends FailBackRegistry {

    private final static String DEFAULT_ROOT = "fastrpc";

    private String root;

    private ConcurrentMap<URL, ConcurrentMap<RegistryListener, ChildListener>> zkListeners = new ConcurrentHashMap<>();

    private ZookeeperClient client;

    public ZookeeperRegistry(URL url, ZookeeperTransporter zookeeperTransporter) {
        super(url);
        String group = url.getParameter(Constants.GROUP_KEY, DEFAULT_ROOT);
        if (!group.startsWith(Constants.PATH_SEPARATOR)) {
            group = Constants.PATH_SEPARATOR + group;
        }
        this.root = group;
        client = zookeeperTransporter.connect(url);
        client.addStateListener(new StateListener() {
            @Override
            public void stateChanged(int state) {
                if (state == RECONNECTED) {
                    try {
                        recover();
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }
            }
        });
    }

    @Override
    protected void doSubscribe(final URL url, final RegistryListener listener) {
        try {
            List<URL> urls = new ArrayList<>();
            for (String path : toCategoriesPath(url)) {
                ConcurrentMap<RegistryListener, ChildListener> listeners = zkListeners.get(url);
                if (listeners == null) {
                    zkListeners.putIfAbsent(url, new ConcurrentHashMap<RegistryListener, ChildListener>());
                    listeners = zkListeners.get(url);
                }
                ChildListener zkListener = listeners.get(listener);
                if (zkListener == null) {
                    listeners.putIfAbsent(listener, new ChildListener() {
                        /**
                         * Listen to '/${root}/${app}/providers' service list.
                         */
                        @Override
                        public void childChanged(String parent, List<String> children) {
                            ZookeeperRegistry.this.notify(url, listener, toUrlsWithEmpty(url, parent, children));
                        }
                    });
                    zkListener = listeners.get(listener);
                }
                client.create(path, false);
                List<String> children = client.addChildListener(path, zkListener);
                if (children != null) {
                    urls.addAll(toUrlsWithEmpty(url, path, children));
                }
            }
            notify(url, listener, urls);
        } catch (Throwable e) {
            throw new RuntimeException("Failed to subscribe " + url + " to zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doUnsubscribe(URL url, RegistryListener listener) {
        ConcurrentMap<RegistryListener, ChildListener> listeners = zkListeners.get(url);
        if (listeners != null) {
            ChildListener zkListener = listeners.get(listener);
            if (zkListener != null) {
                for (String path : toCategoriesPath(url)) {
                    client.removeChildListener(path, zkListener);
                }
            }
        }
    }

    @Override
    protected void doOnline(URL url) {
        try {
            client.create(toUrlPath(url), url.getParameter(Constants.DYNAMIC_KEY, true));
        } catch (Throwable e) {
            throw new RuntimeException("Failed to online " + url + " to zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    protected void doOffline(URL url) {
        try {
            client.delete(toUrlPath(url));
        } catch (Throwable e) {
            throw new RuntimeException("Failed to offline " + url + " to zookeeper " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    protected List<URL> toUrlsWithEmpty(URL consumer, String path, List<String> providers) {
        List<URL> urls = toUrlsWithoutEmpty(consumer, providers);
        if (urls == null || urls.isEmpty()) {
            int i = path.lastIndexOf('/');
            String category = i < 0 ? path : path.substring(i + 1);
            URL empty = consumer.setProtocol(Constants.EMPTY_PROTOCOL).addParameter(Constants.CATEGORY_KEY, category);
            urls.add(empty);
        }
        return urls;
    }

    protected List<URL> toUrlsWithoutEmpty(URL consumer, List<String> providers) {
        List<URL> urls = new ArrayList<>();
        if (providers != null && !providers.isEmpty()) {
            for (String provider : providers) {
                provider = URL.decode(provider);
                if (provider.contains("://")) {
                    URL url = URL.valueOf(provider);
                    if (UrlUtils.isMatch(consumer, url)) {
                        urls.add(url);
                    }
                }
            }
        }
        return urls;
    }

    protected String toUrlPath(URL url) {
        return toCategoryPath(url) + Constants.PATH_SEPARATOR + URL.encode(url.toFullString());
    }

    protected String toCategoryPath(URL url) {
        return toServicePath(url) + Constants.PATH_SEPARATOR + url.getParameter(Constants.CATEGORY_KEY, Constants.PROVIDERS);
    }

    protected String toServicePath(URL url) {
        String application = Constants.SERVER_KEY.equals(url.getParameter(Constants.SIDE_KEY))
                ? url.getParameter(Constants.APPLICATION_KEY)
                : url.getParameter(Constants.REMOTE_APPLICATION_KEY);
        return toRootDir() + application;
    }

    protected String[] toCategoriesPath(URL url) {
        String[] categories = url.getParameter(Constants.CATEGORY_KEY, new String[]{Constants.PROVIDERS});
        String[] paths = new String[categories.length];
        for (int i = 0; i < categories.length; i++) {
            paths[i] = toServicePath(url) + Constants.PATH_SEPARATOR + categories[i];
        }
        return paths;
    }

    protected String toRootDir() {
        if (root.equals(Constants.PATH_SEPARATOR)) {
            return root;
        }
        return root + Constants.PATH_SEPARATOR;
    }

    @Override
    public void destroy() {
        super.destroy();
        try {
            client.destroy();
        } catch (Exception e) {
            logger.warn("Failed to close zookeeper client " + getUrl() + ", cause: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean isActive() {
        return client.isActive();
    }
}
