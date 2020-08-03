package com.fast.fastrpc.common;

/**
 * @author yiji
 * @version : Node.java, v 0.1 2020-07-29
 */
public interface Host extends Destroyable {

    URL getUrl();

    boolean isActive();

}
