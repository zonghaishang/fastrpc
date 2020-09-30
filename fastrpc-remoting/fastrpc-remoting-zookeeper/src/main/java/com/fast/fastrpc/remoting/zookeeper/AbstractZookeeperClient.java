package com.fast.fastrpc.remoting.zookeeper;

import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yiji
 * @version : AbstractZookeeperClient.java, v 0.1 2020-09-30
 */
public abstract class AbstractZookeeperClient<ChildrenListener> implements ZookeeperClient {

    protected static final Logger logger = LoggerFactory.getLogger(AbstractZookeeperClient.class);

    private final URL url;

    private final Set<StateListener> stateListeners = new CopyOnWriteArraySet<StateListener>();

    private final ConcurrentMap<String, ConcurrentMap<ChildListener, ChildrenListener>> childListeners = new ConcurrentHashMap<>();

    private AtomicBoolean destroyed = new AtomicBoolean();

    private final Set<String> persistentExistNodePath = new CopyOnWriteArraySet<>();

    public AbstractZookeeperClient(URL url) {
        this.url = url;
    }

    @Override
    public URL getUrl() {
        return url;
    }


    @Override
    public void delete(String path) {
        persistentExistNodePath.remove(path);
        deletePath(path);
    }


    @Override
    public void create(String path, boolean ephemeral) {

        if (!ephemeral) {
            if (persistentExistNodePath.contains(path)) {
                return;
            }
            if (checkExists(path)) {
                persistentExistNodePath.add(path);
                return;
            }
        }

        int i = path.lastIndexOf('/');
        if (i > 0) {
            create(path.substring(0, i), false);
        }
        if (ephemeral) {
            createEphemeral(path);
        } else {
            createPersistent(path);
            persistentExistNodePath.add(path);
        }
    }

    @Override
    public void addStateListener(StateListener listener) {
        stateListeners.add(listener);
    }

    @Override
    public void removeStateListener(StateListener listener) {
        stateListeners.remove(listener);
    }

    public Set<StateListener> getSessionListeners() {
        return stateListeners;
    }

    @Override
    public List<String> addChildListener(String path, final ChildListener listener) {
        ConcurrentMap<ChildListener, ChildrenListener> listeners = childListeners.get(path);
        if (listeners == null) {
            childListeners.putIfAbsent(path, new ConcurrentHashMap<ChildListener, ChildrenListener>());
            listeners = childListeners.get(path);
        }
        ChildrenListener targetListener = listeners.get(listener);
        if (targetListener == null) {
            listeners.putIfAbsent(listener, createChildrenListener(path, listener));
            targetListener = listeners.get(listener);
        }
        return addChildrenListener(path, targetListener);
    }

    @Override
    public void removeChildListener(String path, ChildListener listener) {
        ConcurrentMap<ChildListener, ChildrenListener> listeners = childListeners.get(path);
        if (listeners != null) {
            ChildrenListener targetListener = listeners.remove(listener);
            if (targetListener != null) {
                removeChildrenListener(path, targetListener);
            }
        }
    }

    protected void stateChanged(int state) {
        for (StateListener sessionListener : getSessionListeners()) {
            sessionListener.stateChanged(state);
        }
    }

    @Override
    public void destroy() {
        if (destroyed.compareAndSet(false, true)) {
            try {
                doDestroy();
            } catch (Throwable t) {
                logger.warn(t.getMessage(), t);
            }
        }
    }

    protected abstract void doDestroy();

    protected abstract void createPersistent(String path);

    protected abstract void createEphemeral(String path);

    protected abstract void deletePath(String path);

    protected abstract boolean checkExists(String path);

    protected abstract ChildrenListener createChildrenListener(String path, ChildListener listener);

    protected abstract List<String> addChildrenListener(String path, ChildrenListener listener);

    protected abstract void removeChildrenListener(String path, ChildrenListener listener);
}

