package com.fast.fastrpc;

/**
 * @author yiji
 * @version : InvokeCallback.java, v 0.1 2020-08-03
 */
public interface InvokeCallback {

    /**
     * done.
     *
     * @param value
     */
    void complete(Object value);

    /**
     * caught exception.
     *
     * @param exception
     */
    void caught(Throwable exception);

}
