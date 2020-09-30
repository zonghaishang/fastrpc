package com.fast.fastrpc.remoting.zookeeper;

import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.spi.SPI;

/**
 * @author yiji
 * @version : ZookeeperTransporter.java, v 0.1 2020-09-30
 */
@SPI("curator")
public interface ZookeeperTransporter {

    ZookeeperClient connect(URL url);

}
