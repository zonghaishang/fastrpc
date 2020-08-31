package com.fast.fastrpc.filter;


import com.fast.fastrpc.Filter;
import com.fast.fastrpc.Invocation;
import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.Result;
import com.fast.fastrpc.RpcException;

/**
 * @author yiji
 * @version : ExceptionFilter.java, v 0.1 2020-08-31
 */
public class ExceptionFilter implements Filter {

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        return null;
    }
}
