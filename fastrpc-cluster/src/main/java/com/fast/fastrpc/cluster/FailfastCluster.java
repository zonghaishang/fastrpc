package com.fast.fastrpc.cluster;

import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.RpcException;

/**
 * @author yiji
 * @version : FailFastCluster.java, v 0.1 2020-10-13
 */
public class FailfastCluster implements Cluster {

    public final static String NAME = "failfast";

    @Override
    public Invoker join(Directory directory) throws RpcException {
        return null;
    }

}
