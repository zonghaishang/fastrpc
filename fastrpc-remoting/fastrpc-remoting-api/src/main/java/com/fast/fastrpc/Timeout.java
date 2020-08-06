package com.fast.fastrpc;

/**
 * @author yiji
 * @version : Timeout.java, v 0.1 2020-08-06
 */
public interface Timeout {

    boolean isExpired();

    boolean isCancelled();

    boolean cancel();

}
