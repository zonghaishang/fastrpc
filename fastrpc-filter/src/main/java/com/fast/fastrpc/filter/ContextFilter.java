package com.fast.fastrpc.filter;

import com.fast.fastrpc.Filter;
import com.fast.fastrpc.Invocation;
import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.Result;
import com.fast.fastrpc.RpcContext;
import com.fast.fastrpc.RpcException;
import com.fast.fastrpc.RpcInvocation;
import com.fast.fastrpc.RpcResult;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.spi.Activate;
import com.fast.fastrpc.common.spi.Order;

/**
 * @author yiji
 * @version : ContextFilter.java, v 0.1 2020-09-01
 */
@Activate(group = {Constants.SERVER_KEY}, order = Order.Highest - 1100)
public class ContextFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            if (invocation instanceof RpcInvocation) {
                ((RpcInvocation) invocation).setInvoker(invoker);
            }
            RpcResult result = (RpcResult) invoker.invoke(invocation);
            return result;
        } finally {
            RpcContext.removeContext();
        }
    }

}
