package com.fast.fastrpc.registry;

import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.URL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author yiji
 * @version : AbstractRegistryFactory.java, v 0.1 2020-09-30
 */
public abstract class AbstractRegistryFactory implements RegistryFactory {

    private static final ReentrantLock LOCK = new ReentrantLock();

    // Registry Collection Map<RegistryAddress, Registry>
    private static final Map<String, Registry> REGISTRIES = new ConcurrentHashMap<String, Registry>();

    @Override
    public Registry getRegistry(URL url) {
        url = url.setPath(Registry.class.getName())
                .addParameter(Constants.INTERFACE_KEY, Registry.class.getName())
                .removeParameters(Constants.EXPORT_KEY, Constants.REFER_KEY);
        String key = url.toServiceString();
        LOCK.lock();
        try {
            Registry registry = REGISTRIES.get(key);
            if (registry != null) {
                return registry;
            }
            registry = createRegistry(url);
            if (registry == null) {
                throw new IllegalStateException("Can not create registry " + url);
            }
            REGISTRIES.put(key, registry);
            return registry;
        } finally {
            LOCK.unlock();
        }
    }

    protected abstract Registry createRegistry(URL url);

}
