package com.fast.fastrpc.registry.zookeeper;

import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.registry.AbstractRegistryFactory;
import com.fast.fastrpc.registry.Registry;
import com.fast.fastrpc.remoting.zookeeper.ZookeeperTransporter;

/**
 * @author yiji
 * @version : ZookeeperRegistryFactory.java, v 0.1 2020-09-30
 */
public class ZookeeperRegistryFactory extends AbstractRegistryFactory {

    private ZookeeperTransporter zookeeperTransporter;

    public void setZookeeperTransporter(ZookeeperTransporter zookeeperTransporter) {
        this.zookeeperTransporter = zookeeperTransporter;
    }


    @Override
    protected Registry createRegistry(URL url) {
        return new ZookeeperRegistry(url, zookeeperTransporter);
    }

}
