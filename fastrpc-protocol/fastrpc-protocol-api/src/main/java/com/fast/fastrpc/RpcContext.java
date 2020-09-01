package com.fast.fastrpc;

import java.net.SocketAddress;

/**
 * @author yiji
 * @version : RpcContext.java, v 0.1 2020-09-01
 */
public class RpcContext {

    private final static ThreadLocal<RpcContext> context = new InheritableThreadLocal() {
        @Override
        protected Object initialValue() {
            return new RpcContext();
        }
    };

    private SocketAddress remoteAddress;

    public static RpcContext getContext() {
        return context.get();
    }

    public static void removeContext() {
        context.remove();
    }

    public SocketAddress remoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(SocketAddress remoteAddress) {
        this.remoteAddress = remoteAddress;
    }
}
