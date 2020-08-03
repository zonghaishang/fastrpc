package com.fast.fastrpc;

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

    /**
     * set callback.
     *
     * @param callback
     */
    void setCallback(InvokeCallback callback);

    /**
     * get callback.
     */
    InvokeCallback getCallback();

    /**
     * check is done.
     *
     * @return done or not.
     */
    boolean isDone();

}
