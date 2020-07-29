package com.fast.fastrpc;

import com.fast.fastrpc.common.Destroyable;

/**
 * @author yiji
 * @version : Exporter.java, v 0.1 2020-07-28
 */
public interface Exporter<T> extends Destroyable {

    Invoker<T> getInvoker();

}
