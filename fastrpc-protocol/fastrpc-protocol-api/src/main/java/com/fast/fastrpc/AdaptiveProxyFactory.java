package com.fast.fastrpc;

import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.spi.Adaptive;
import com.fast.fastrpc.common.spi.ExtensionLoader;

/**
 * @author yiji
 * @version : AdaptiveProxyFactory.java, v 0.1 2020-10-03
 */
@Adaptive
public class AdaptiveProxyFactory implements ProxyFactory {

    private final ExtensionLoader<ProxyFactory> factoryLoader = ExtensionLoader.getExtensionLoader(ProxyFactory.class);

    @Override
    public <T> T getProxy(Invoker<T> invoker) throws RpcException {
        final String proxy = invoker.getUrl().getParameter(Constants.PROXY_KEY, Constants.PROXY_ASM);
        return factoryLoader.getExtension(proxy).getProxy(invoker);
    }

    @Override
    public <T> Invoker<T> getInvoker(T target, Class<T> type, URL url) throws RpcException {
        final String proxy = url.getParameter(Constants.PROXY_KEY, Constants.PROXY_ASM);
        return factoryLoader.getExtension(proxy).getInvoker(target, type, url);
    }

}
