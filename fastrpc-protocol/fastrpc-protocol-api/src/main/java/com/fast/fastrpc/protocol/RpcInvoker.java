package com.fast.fastrpc.protocol;

import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.common.URL;

import java.util.Map;

/**
 * @author yiji
 * @version : RpcInvoker.java, v 0.1 2020-07-30
 */
public abstract class RpcInvoker<T> extends AbstractInvoker<T> {

    protected Invoker<T> target;

    public RpcInvoker(Invoker<T> invoker, Class<T> type, URL url) {
        this(invoker, type, url, (Map<String, String>) null);
    }

    public RpcInvoker(Invoker<T> invoker, Class<T> type, URL url, String[] keys) {
        this(invoker, type, url, convertAttachment(url, keys));
    }

    public RpcInvoker(Invoker<T> invoker, Class<T> type, URL url, Map<String, String> attachment) {
        super(type, url, attachment);
        this.target = invoker;
    }

    @Override
    public T getProxy() {
        return target.getProxy();
    }
}
