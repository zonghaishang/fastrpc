package com.fast.fastrpc.cluster.loadbalance;

import com.fast.fastrpc.Invocation;
import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.RpcException;
import com.fast.fastrpc.cluster.LoadBalance;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.URL;

import java.util.List;

/**
 * @author yiji
 * @version : AbstractLoadBalance.java, v 0.1 2020-10-13
 */
public abstract class AbstractLoadBalance implements LoadBalance {

    @Override
    public <T> Invoker<T> select(List<Invoker<T>> invokers, Invocation invocation, URL url) throws RpcException {
        if (invokers == null || invokers.isEmpty())
            return null;
        if (invokers.size() == 1)
            return invokers.get(0);
        return doSelect(invokers, invocation, url);
    }

    protected int getWeight(Invoker<?> invoker, Invocation invocation) {
        int weight = invoker.getUrl().getMethodParameter(invocation.getMethodName(), Constants.WEIGHT_KEY, Constants.DEFAULT_WEIGHT);
        if (weight > 0) {
            int warmup = invoker.getUrl().getParameter(Constants.WARMUP_KEY, 0);
            long timestamp = invoker.getUrl().getParameter(Constants.REMOTE_TIMESTAMP_KEY, 0);
            if (warmup > 0 && timestamp > 0) {
                int uptime = (int) (System.currentTimeMillis() - timestamp);
                return uptime < warmup ? getWarmupWeight(uptime, warmup, weight) : weight;
            }
        }
        return weight;
    }

    protected int getWarmupWeight(int uptime, int warmup, int weight) {
        int warmupWeight = (int) (((float) uptime / (float) warmup) * (float) weight);
        return warmupWeight < 1 ? 1 : (warmupWeight > weight ? weight : warmupWeight);
    }

    protected abstract <T> Invoker<T> doSelect(List<Invoker<T>> invokers, Invocation invocation, URL url);
}
