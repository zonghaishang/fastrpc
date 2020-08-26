package com.fast.fastrpc.protocol;

import com.fast.fastrpc.Invocation;
import com.fast.fastrpc.ReferenceCountedClient;
import com.fast.fastrpc.Result;
import com.fast.fastrpc.common.URL;

import java.util.List;


/**
 * @author yiji
 * @version : FastInvoker.java, v 0.1 2020-08-26
 */
public class FastInvoker<T> extends AbstractInvoker<T> {

    private List<ReferenceCountedClient> clients;

    public FastInvoker(Class<T> type, URL url, List<ReferenceCountedClient> clients) {
        super(type, url);
        this.clients = clients;
    }

    @Override
    protected Result doInvoke(Invocation invocation) throws Throwable {
        return null;
    }

    @Override
    public T getProxy() {
        return null;
    }

    @Override
    public void destroy() {
        if (isDestroyed()) return;
        super.destroy();
        for (ReferenceCountedClient client : clients) {
            try {
                client.destroy();
            } catch (Throwable t) {
                if (logger.isWarnEnabled()) {
                    logger.warn(t.getMessage(), t);
                }
            }
        }
    }
}
