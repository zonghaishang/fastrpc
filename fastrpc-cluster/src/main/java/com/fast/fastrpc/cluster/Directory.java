package com.fast.fastrpc.cluster;

import com.fast.fastrpc.Invocation;
import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.RpcException;
import com.fast.fastrpc.common.Host;

import java.util.List;

/**
 * @author yiji
 * @version : Directory.java, v 0.1 2020-10-13
 */
public interface Directory<T> extends Host {

    /**
     * get service type.
     *
     * @return service type.
     */
    Class<T> getInterface();

    /**
     * list invokers.
     *
     * @return invokers
     */
    List<Invoker<T>> list(Invocation invocation) throws RpcException;

}
