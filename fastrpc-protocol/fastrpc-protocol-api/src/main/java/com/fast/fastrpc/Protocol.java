package com.fast.fastrpc;

import com.fast.fastrpc.common.Destroyable;
import com.fast.fastrpc.common.URL;

/**
 * @author yiji
 * @version : Protocol.java, v 0.1 2020-07-29
 */
public interface Protocol extends Destroyable {

    <T> Exporter<T> export(Invoker<T> invoker) throws RpcException;

    <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException;

}
