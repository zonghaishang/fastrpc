package com.fast.fastrpc.cluster;

import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.RpcException;
import com.fast.fastrpc.common.spi.SPI;

/**
 * @author yiji
 * @version : Cluster.java, v 0.1 2020-10-13
 */
@SPI(FailoverCluster.NAME)
public interface Cluster<T> {

    <T> Invoker<T> join(Directory<T> directory) throws RpcException;

}
