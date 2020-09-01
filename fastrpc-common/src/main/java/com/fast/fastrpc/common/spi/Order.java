package com.fast.fastrpc.common.spi;

/**
 * @author yiji
 * @version : Order.java, v 0.1 2020-07-15
 */
public interface Order {

    int order();

    int Lowest = -10000;

    int Normal = 1;

    int Highest = 10000;

}
