package com.fast.fastrpc.protocol;

import com.fast.fastrpc.ExchangeHandler;
import com.fast.fastrpc.Exchangers;
import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.RpcException;
import com.fast.fastrpc.Server;
import com.fast.fastrpc.common.Destroyable;
import com.fast.fastrpc.common.URL;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yiji
 * @version : AbstractFastProtocol.java, v 0.1 2020-08-18
 */
public abstract class AbstractFastProtocol extends AbstractProtocol {

    protected abstract String getVersion();

    protected static final Map<String, Server> startedServers = new ConcurrentHashMap<>();

    protected static final AtomicInteger exportedCount = new AtomicInteger();

    protected void openServer(URL url) {
        String host = url.getAddress();

        /*
         * the service port may already be open.
         */
        if (startedServers.containsKey(host)) {
            return;
        }

        synchronized (startedServers) {
            if (!startedServers.containsKey(host)) {
                startedServers.put(host, createServer(url));
            }
        }
    }

    protected Server createServer(URL url) {
        Server server;
        try {
            server = Exchangers.bind(url, getHandler());
        } catch (RemotingException e) {
            throw new RpcException("Fail to start server, url: " + url + ", message: " + e.getMessage(), e);
        }
        return server;
    }

    @Override
    protected <T> Destroyable doExport(final Invoker<T> invoker) throws RpcException {
        return new DestroyableExporter(invoker);
    }

    protected class DestroyableExporter implements Destroyable {
        final Invoker invoker;

        public DestroyableExporter(Invoker invoker) {
            this.invoker = invoker;
            // increase the service exposure count,
            // if the service count is reduced to 0, the protocol is destroyed..
            exportedCount.incrementAndGet();
        }

        @Override
        public void destroy() {
            if (exportedCount.decrementAndGet() <= 0) {
                AbstractFastProtocol.this.destroy();
                String host = invoker.getUrl().getHost();
                Server server = startedServers.remove(host);
                if (server != null) {
                    server.destroy();
                }
            }
        }
    }

    protected abstract ExchangeHandler getHandler();
}
