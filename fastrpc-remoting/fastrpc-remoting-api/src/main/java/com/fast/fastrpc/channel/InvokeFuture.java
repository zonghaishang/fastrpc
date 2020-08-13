package com.fast.fastrpc.channel;

import com.fast.fastrpc.RemotingException;

/**
 * @author yiji
 * @version : InvokeFuture.java, v 0.1 2020-08-03
 */
public interface InvokeFuture {

    /**
     * get result.
     *
     * @return result.
     */
    Object get() throws RemotingException;

    /**
     * get result with the specified timeout.
     *
     * @param timeout timeout(milliseconds).
     * @return result.
     */
    Object get(int timeout) throws RemotingException;

    int invokeId();

    /**
     * set callback.
     *
     * @param callback
     */
    void addListener(InvokeListener callback);

    /**
     * check is done.
     *
     * @return done or not.
     */
    boolean isDone();

    /**
     * check is success.
     *
     * @return true success or failed.
     */
    boolean isSuccess();

    Throwable cause();

}
