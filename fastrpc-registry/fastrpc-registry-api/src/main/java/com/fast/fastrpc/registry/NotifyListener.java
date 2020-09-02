package com.fast.fastrpc.registry;

import com.fast.fastrpc.common.URL;

import java.util.List;

/**
 * @author yiji
 * @version : NotifyListener.java, v 0.1 2020-09-02
 */
public interface NotifyListener {

    void notify(List<URL> urls);

}
