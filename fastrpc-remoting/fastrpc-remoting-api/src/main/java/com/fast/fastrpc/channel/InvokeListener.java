package com.fast.fastrpc.channel;

/**
 * @author yiji
 * @version : InvokeCallback.java, v 0.1 2020-08-03
 */
public interface InvokeListener {

    void complete(InvokeFuture future);

}
