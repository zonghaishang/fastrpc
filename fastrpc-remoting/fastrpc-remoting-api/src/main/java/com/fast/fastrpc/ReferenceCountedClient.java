package com.fast.fastrpc;

import com.fast.fastrpc.channel.Attribute;
import com.fast.fastrpc.channel.AttributeKey;
import com.fast.fastrpc.channel.ChannelPromise;
import com.fast.fastrpc.channel.InvokeFuture;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.buffer.IoBuffer;

import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yiji
 * @version : ReferencedCountClient.java, v 0.1 2020-08-26
 */
public class ReferenceCountedClient implements Client {

    private Client client;

    private AtomicInteger count = new AtomicInteger();

    public ReferenceCountedClient(Client client) {
        this.client = client;
        this.count.incrementAndGet();
    }

    @Override
    public void connect() throws RemotingException {
        this.client.connect();
    }

    @Override
    public SocketAddress localAddress() {
        return this.client.localAddress();
    }

    @Override
    public SocketAddress remoteAddress() {
        return this.client.remoteAddress();
    }

    @Override
    public void write(Object msg, ChannelPromise promise) throws RemotingException {
        this.client.write(msg, promise);
    }

    @Override
    public IoBuffer allocate() {
        return this.client.allocate();
    }

    @Override
    public IoBuffer allocate(int capacity) {
        return this.client.allocate(capacity);
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return this.client.attr(key);
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {
        return this.client.hasAttr(key);
    }

    @Override
    public URL getUrl() {
        return this.client.getUrl();
    }

    @Override
    public boolean isActive() {
        return this.client.isActive();
    }

    @Override
    public InvokeFuture shutdown() {
        return this.shutdown(0);
    }

    @Override
    public InvokeFuture shutdown(int timeout) {
        if (this.count.get() <= 0) {
            return this.client.shutdown(timeout);
        }
        return null;
    }

    @Override
    public void destroy() {
        if (this.count.decrementAndGet() <= 0) {
            this.client.destroy();
        }
    }

    public int incrementAndGet() {
        return this.count.incrementAndGet();
    }
}
