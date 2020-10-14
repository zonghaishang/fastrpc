package com.fast.fastrpc.cluster.loadbalance;

import com.fast.fastrpc.Invocation;
import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.common.URL;

import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * @author yiji
 * @version : RandomLoadBalance.java, v 0.1 2020-10-13
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    private final Random random = ThreadLocalRandom.current();

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, Invocation invocation, URL url) {
        int length = invokers.size();
        int totalWeight = 0;
        int firstWeight = 0;
        boolean sameWeight = true;
        for (int i = 0; i < length; i++) {
            int weight = getWeight(invokers.get(i), invocation);
            if (i == 0) {
                firstWeight = weight;
            }
            totalWeight += weight;
            if (sameWeight && i > 0 && weight != firstWeight) {
                sameWeight = false;
            }
        }
        if (totalWeight > 0 && !sameWeight) {
            int offset = random.nextInt(totalWeight);
            for (int i = 0; i < length; i++) {
                offset -= getWeight(invokers.get(i), invocation);
                if (offset < 0) {
                    return invokers.get(i);
                }
            }
        }
        return invokers.get(random.nextInt(length));
    }
}
