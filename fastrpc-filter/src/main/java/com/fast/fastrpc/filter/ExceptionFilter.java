package com.fast.fastrpc.filter;


import com.fast.fastrpc.Filter;
import com.fast.fastrpc.Invocation;
import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.Result;
import com.fast.fastrpc.RpcContext;
import com.fast.fastrpc.RpcException;
import com.fast.fastrpc.RpcResult;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.common.utils.ExceptionUtils;

/**
 * @author yiji
 * @version : ExceptionFilter.java, v 0.1 2020-08-31
 */
public class ExceptionFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(ExceptionFilter.class);

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        try {
            Result result = invoker.invoke(invocation);
            if (result.hasException()) {
                try {
                    Throwable exception = result.getException();
                    if (!(exception instanceof RuntimeException) && (exception instanceof Exception)) {
                        return result;
                    }

                    logger.error("Got unchecked exception which called by " + RpcContext.getContext().remoteAddress()
                            + ", service: " + invoker.getInterface().getName()
                            + ", method: " + invocation.getMethodName()
                            + ", exception: " + exception.getMessage(), exception);

                    String className = exception.getClass().getName();
                    if (className.startsWith("java.") || className.startsWith("javax.")) {
                        return result;
                    }

                    if (exception instanceof RpcException) {
                        return result;
                    }

                    return new RpcResult(new RuntimeException(ExceptionUtils.toString(exception)));
                } catch (Throwable e) {
                    logger.warn("Fail to execute ExceptionFilter when called by " + RpcContext.getContext().remoteAddress()
                            + ", service: " + invoker.getInterface().getName()
                            + ", method: " + invocation.getMethodName()
                            + ", exception: " + e.getMessage(), e);
                    return result;
                }
            }
            return result;
        } catch (RuntimeException e) {
            logger.error("Got unchecked exception which called by " + RpcContext.getContext().remoteAddress()
                    + ", service: " + invoker.getInterface().getName()
                    + ", method: " + invocation.getMethodName()
                    + ", exception: " + e.getMessage(), e);
            throw e;
        }
    }
}
