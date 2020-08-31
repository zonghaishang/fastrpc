package com.fast.fastrpc;

import com.fast.fastrpc.common.spi.SPI;

/**
 * @author yiji
 * @version : Filter.java, v 0.1 2020-08-31
 */
@SPI
public interface Filter {

    /**
     * rpc invoke interceptor.
     * <p>
     * If you expect the subsequent process to finish,
     * please continue to call the `invoker.invoke` method
     * <p>
     *
     * <code>
     * // do something before invoke
     * Result result = invoker.invoke(invocation);
     * // do something after invoked
     * return result;
     * </code>
     *
     * @param invoker    target service
     * @param invocation target service arguments.
     * @return rpc invoke result.
     * @throws RpcException
     */
    Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException;

}
