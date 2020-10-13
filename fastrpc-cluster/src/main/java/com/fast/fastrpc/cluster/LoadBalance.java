package com.fast.fastrpc.cluster;

import com.fast.fastrpc.Invocation;
import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.RpcException;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.spi.SPI;

import java.util.List;

/**
 * @author yiji
 * @version : LoadBalance.java, v 0.1 2020-10-13
 */
@SPI
public interface LoadBalance {

    /**
     * support rpc load balance capability.
     *
     * @param invokers   rpc invokers
     * @param invocation invoke information
     * @param url        refer url
     * @param <T>        invoker type
     * @return selected invoker
     * @throws RpcException
     */
    <T> Invoker<T> select(List<Invoker<T>> invokers, Invocation invocation, URL url) throws RpcException;

}
