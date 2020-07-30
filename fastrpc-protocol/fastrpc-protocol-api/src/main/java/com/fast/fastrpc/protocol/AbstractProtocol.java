package com.fast.fastrpc.protocol;

import com.fast.fastrpc.Exporter;
import com.fast.fastrpc.Invocation;
import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.Protocol;
import com.fast.fastrpc.ProxyFactory;
import com.fast.fastrpc.Result;
import com.fast.fastrpc.RpcException;
import com.fast.fastrpc.common.Destroyable;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.common.utils.ServiceUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author yiji
 * @version : AbstractProtocol.java, v 0.1 2020-07-29
 */
public abstract class AbstractProtocol implements Protocol {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected final Map<String, Exporter<?>> exporterMap = new ConcurrentHashMap<>();
    protected final Set<Invoker<?>> invokers = new CopyOnWriteArraySet<>();

    protected ProxyFactory proxyFactory;

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        final String key = findServiceKey(invoker.getUrl());
        Exporter<T> exporter = (Exporter<T>) exporterMap.get(key);
        if (exporter != null) {
            return exporter;
        }

        RpcExporter rpcExporter = new RpcExporter<>(invoker, key, exporterMap);
        rpcExporter.setDestroyable(doExport(invoker));
        exporterMap.put(key, rpcExporter);
        return rpcExporter;
    }

    @Override
    public <T> Invoker<T> refer(final Class<T> type, final URL url) throws RpcException {
        Invoker<T> invoker = new RpcInvoker<T>(doRefer(type, url), type, url) {
            @Override
            protected Result doInvoke(Invocation invocation) {
                try {
                    return this.target.invoke(invocation);
                } catch (RpcException e) {
                    if (e.getCode() == RpcException.UNKNOWN_EXCEPTION) {
                        e.setCode(getErrorCode(e.getCause()));
                    }
                    throw e;
                } catch (Throwable e) {
                    throw getRpcException(type, url, invocation, e);
                }
            }
        };
        invokers.add(invoker);
        return invoker;
    }

    @Override
    public void destroy() {
        for (Iterator<Invoker<?>> iterator = invokers.iterator(); iterator.hasNext(); iterator.remove()) {
            Invoker<?> invoker = iterator.next();
            try {
                if (logger.isInfoEnabled()) {
                    logger.info("Destroy reference: " + invoker.getUrl());
                }
                invoker.destroy();
            } catch (Throwable t) {
                logger.warn(t.getMessage(), t);
            }
        }

        for (Iterator<Exporter<?>> iterator = exporterMap.values().iterator(); iterator.hasNext(); iterator.remove()) {
            Exporter<?> exporter = iterator.next();
            try {
                if (logger.isInfoEnabled()) {
                    logger.info("Destroy service: " + exporter.getInvoker().getUrl());
                }
                exporter.destroy();
            } catch (Throwable t) {
                logger.warn(t.getMessage(), t);
            }
        }
    }

    protected String findServiceKey(URL url) {
        return ServiceUtils.findServiceKey(url);
    }

    protected int getErrorCode(Throwable e) {
        return RpcException.UNKNOWN_EXCEPTION;
    }

    protected RpcException getRpcException(Class<?> type, URL url, Invocation invocation, Throwable e) {
        RpcException re = new RpcException("Failed to invoke remote service: " + type + ", method: "
                + invocation.getMethodName() + ", cause: " + e.getMessage(), e);
        re.setCode(getErrorCode(e));
        return re;
    }


    public ProxyFactory getProxyFactory() {
        return proxyFactory;
    }

    public void setProxyFactory(ProxyFactory proxyFactory) {
        this.proxyFactory = proxyFactory;
    }

    // need to be override.

    protected abstract <T> Destroyable doExport(Invoker<T> invoker) throws RpcException;

    protected abstract <T> Invoker<T> doRefer(Class<T> type, URL url) throws RpcException;
}
