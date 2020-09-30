package com.fast.fastrpc.remoting.zookeeper;

import com.fast.fastrpc.common.Host;

import java.util.List;

/**
 * @author yiji
 * @version : ZookeeperClient.java, v 0.1 2020-09-30
 */
public interface ZookeeperClient extends Host {

    void create(String path, boolean ephemeral);

    void delete(String path);

    List<String> getChildren(String path);

    List<String> addChildListener(String path, ChildListener listener);

    void removeChildListener(String path, ChildListener listener);

    void addStateListener(StateListener listener);

    void removeStateListener(StateListener listener);

}
