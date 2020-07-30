package com.fast.fastrpc;

import com.fast.fastrpc.common.URL;

/**
 * @author yiji
 * @version : ProxyFactory.java, v 0.1 2020-07-30
 */
public interface ProxyFactory {

    /**
     * create proxy.
     *
     * @param invoker
     * @return proxy
     */
    <T> T getProxy(Invoker<T> invoker) throws RpcException;

    /**
     * create invoker.
     *
     * @param <T>
     * @param target
     * @param type
     * @param url
     * @return invoker
     */
    <T> Invoker<T> getInvoker(T target, Class<T> type, URL url) throws RpcException;

}
