package com.fast.fastrpc.protocol;

import com.fast.fastrpc.Client;
import com.fast.fastrpc.ExchangeHandler;
import com.fast.fastrpc.Exchangers;
import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.ReferenceCountedClient;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.RpcException;
import com.fast.fastrpc.Server;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.Destroyable;
import com.fast.fastrpc.common.URL;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yiji
 * @version : AbstractFastProtocol.java, v 0.1 2020-08-18
 */
public abstract class AbstractFastProtocol extends AbstractProtocol {

    protected abstract String getVersion();

    protected static final Map<String, Server> startedServers = new ConcurrentHashMap<>();

    protected static final Map<String, List<ReferenceCountedClient>> startedClients = new ConcurrentHashMap<>();

    protected static final AtomicInteger exportedCount = new AtomicInteger();

    protected List<ReferenceCountedClient> openClient(URL url) {
        List<ReferenceCountedClient> activeClients = new ArrayList<>();
        String host = url.getAddress();

        int connections = url.getParameter(Constants.CONNECTIONS_KEY, 1);
        List<ReferenceCountedClient> clients = startedClients.get(host);

        // the client may already be open.
        if (clients != null && clients.size() >= connections) {
            if (connections == 1) {
                ReferenceCountedClient client = clients.get(0);
                client.incrementAndGet();
                activeClients.add(client);
                return activeClients;
            }

            int count = clients.size();
            Random random = new Random();
            while (connections-- > 0) {
                ReferenceCountedClient client = clients.get(random.nextInt(count));
                client.incrementAndGet();
                activeClients.add(client);
            }
            return activeClients;
        }

        synchronized (startedClients) {

            if (!startedClients.containsKey(host)) {
                startedClients.put(host, new CopyOnWriteArrayList<ReferenceCountedClient>());
            }

            clients = startedClients.get(host);
            int current = clients.size();
            int capacity = connections - current;

            activeClients.addAll(clients.subList(0, current));
            // client connection count has been reached.
            if (capacity <= 0) {
                return activeClients;
            }

            for (int i = 0; i < capacity; i++) {
                ReferenceCountedClient client = new ReferenceCountedClient(createClient(url));
                clients.add(client);
                activeClients.add(client);
            }
            return activeClients;
        }
    }

    protected Client createClient(URL url) {
        Client client;
        try {
            client = Exchangers.connect(url, getHandler());
        } catch (RemotingException e) {
            throw new RpcException("Fail to start client, url: " + url + ", message: " + e.getMessage(), e);
        }
        return client;
    }

    protected List<ReferenceCountedClient> getClients(URL url) {
        return startedClients.get(url.getAddress());
    }

    protected void openServer(URL url) {
        String host = url.getAddress();

        //  the service port may already be open.
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

        private final Invoker<?> invoker;

        public DestroyableExporter(Invoker<?> invoker) {
            this.invoker = invoker;
            /*
             * increase the service exposure count,
             * if the service count is reduced to 0,
             * the protocol will be destroyed.
             */
            exportedCount.incrementAndGet();
        }

        @Override
        public void destroy() {
            if (exportedCount.decrementAndGet() <= 0) {
                AbstractFastProtocol.this.destroy();
                String host = this.invoker.getUrl().getHost();
                Server server = startedServers.remove(host);
                if (server != null) {
                    server.destroy();
                }
            }
        }
    }

    protected abstract ExchangeHandler getHandler();
}
