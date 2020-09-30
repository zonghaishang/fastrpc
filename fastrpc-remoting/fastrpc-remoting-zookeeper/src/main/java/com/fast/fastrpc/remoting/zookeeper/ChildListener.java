package com.fast.fastrpc.remoting.zookeeper;

import java.util.List;

/**
 * @author yiji
 * @version : ChildListener.java, v 0.1 2020-09-30
 */
public interface ChildListener {

    void childChanged(String path, List<String> children);

}
