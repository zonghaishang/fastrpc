package com.fast.fastrpc;

import com.fast.fastrpc.common.Destroyable;
import com.fast.fastrpc.common.URL;

/**
 * @author yiji
 * @version : Protocol.java, v 0.1 2020-07-29
 */
public interface Protocol extends Destroyable {

    // protocol name
    String getName();

    // protocol version, if sub protocols are not supported, null can be returned
    String getVersion();

    // protocol port number
    int getDefaultPort();

    <T> Exporter<T> export(Invoker<T> invoker) throws RpcException;

    <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException;
}
