package com.fast.fastrpc;

import com.fast.fastrpc.common.Host;

/**
 * @author yiji
 * @version : Invoker.java, v 0.1 2020-07-28
 */
public interface Invoker<T> extends Host {

    Result invoke(Invocation invocation) throws RpcException;

    Class<T> getInterface();

    /**
     * Gets the object held by invoker,
     * On the server side, for example, return the interface implementation.
     *
     * @return the real proxy object
     */
    T getProxy();
}
