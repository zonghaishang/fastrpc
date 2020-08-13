package com.fast.fastrpc.channel;

/**
 * @author yiji
 * @version : OperationPromise.java, v 0.1 2020-08-13
 */
public interface ChannelPromise<F extends ChannelFuture<?>> {

    void complete(F future);

}
