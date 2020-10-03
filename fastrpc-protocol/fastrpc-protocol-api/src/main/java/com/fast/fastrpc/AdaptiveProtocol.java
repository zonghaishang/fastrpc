package com.fast.fastrpc;

import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.spi.Adaptive;
import com.fast.fastrpc.common.spi.ExtensionLoader;

/**
 * @author yiji
 * @version : AdaptiveProtocol.java, v 0.1 2020-10-03
 */
@Adaptive
public class AdaptiveProtocol implements Protocol {

    private final ExtensionLoader<Protocol> factoryLoader = ExtensionLoader.getExtensionLoader(Protocol.class);

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        return factoryLoader.getExtension(invoker.getUrl().getProtocol()).export(invoker);
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        return factoryLoader.getExtension(url.getProtocol()).refer(type, url);
    }

    @Override
    public void destroy() {
        throw new UnsupportedOperationException("adaptive protocol not support yet.");
    }

    @Override
    public int getDefaultPort() {
        throw new UnsupportedOperationException("adaptive protocol not support yet.");
    }
}
