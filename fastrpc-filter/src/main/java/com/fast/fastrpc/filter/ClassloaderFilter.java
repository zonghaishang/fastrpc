package com.fast.fastrpc.filter;

import com.fast.fastrpc.Filter;
import com.fast.fastrpc.Invocation;
import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.Result;
import com.fast.fastrpc.RpcException;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.spi.Activate;
import com.fast.fastrpc.common.spi.Order;

/**
 * @author yiji
 * @version : ClassloaderFilter.java, v 0.1 2020-09-01
 */
@Activate(group = Constants.SERVER_KEY, order = Order.Highest - 1000)
public class ClassloaderFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        ClassLoader prev = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(invoker.getInterface().getClassLoader());
            return invoker.invoke(invocation);
        } finally {
            Thread.currentThread().setContextClassLoader(prev);
        }
    }

}
