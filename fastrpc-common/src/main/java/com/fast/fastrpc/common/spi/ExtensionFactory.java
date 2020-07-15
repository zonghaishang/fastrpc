package com.fast.fastrpc.common.spi;

/**
 * @author yiji
 * @version : ExtensionFactory.java, v 0.1 2020-07-15
 */
public interface ExtensionFactory {

    <T> T getExtension(Class<T> type, String name);

}
