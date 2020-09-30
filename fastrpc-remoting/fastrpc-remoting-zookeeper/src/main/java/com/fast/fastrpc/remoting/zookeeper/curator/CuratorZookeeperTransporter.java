package com.fast.fastrpc.remoting.zookeeper.curator;

import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.remoting.zookeeper.ZookeeperClient;
import com.fast.fastrpc.remoting.zookeeper.ZookeeperTransporter;

/**
 * @author yiji
 * @version : CuratorZookeeperTransporter.java, v 0.1 2020-09-30
 */
public class CuratorZookeeperTransporter implements ZookeeperTransporter {
    @Override
    public ZookeeperClient connect(URL url) {
        return new CuratorZookeeperClient(url);
    }
}
