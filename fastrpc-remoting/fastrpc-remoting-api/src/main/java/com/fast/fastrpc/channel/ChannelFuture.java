package com.fast.fastrpc.channel;

/**
 * @author yiji
 * @version : ChannelFuture.java, v 0.1 2020-08-13
 */
public interface ChannelFuture<T> {

    boolean isSuccess();

    Throwable cause();

}
