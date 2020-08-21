package com.fast.fastrpc.transporter;

import com.fast.fastrpc.ChannelHandler;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.Server;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.channel.ChannelPromise;
import com.fast.fastrpc.channel.InvokeFuture;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.buffer.IoBuffer;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yiji
 * @version : AbstractServer.java, v 0.1 2020-08-05
 */
public abstract class AbstractServer extends AbstractPeer implements Server {

    protected InetSocketAddress address;

    protected volatile Channel channel;

    protected AtomicBoolean started = new AtomicBoolean();

    public AbstractServer(URL url, ChannelHandler handler) throws RemotingException {
        super(url, handler);
        // ready to start server.
        start();
    }

    @Override
    public void start() throws RemotingException {
        if (started.compareAndSet(false, true)) {
            this.address = new InetSocketAddress(getUrl().getHost(), getUrl().getPort());
            try {
                this.channel = doBind();
                if (logger.isInfoEnabled()) {
                    logger.info("success to start server on " + this.address);
                }
            } catch (Throwable e) {
                throw new RemotingException(this.channel, "Failed to start server.");
            }
        }
    }

    @Override
    public void write(Object msg, ChannelPromise promise) throws RemotingException {
        this.handler.write(this.channel, msg);
        for (Channel channel : getChannels()) {
            if (channel.isActive()) {
                try {
                    channel.write(msg, null);
                } catch (Exception e) {
                    logger.warn("Failed to write message by channel: " + channel, e);
                }
            }
        }
    }

    @Override
    public InvokeFuture shutdown() {
        return shutdown(this.shutdownTimeout);
    }

    @Override
    public InvokeFuture shutdown(int timeout) {
        Channel channel = this.channel;
        InvokeFuture future = null;
        if (channel != null) future = channel.shutdown();
        doShutdown(timeout);
        return future;
    }

    @Override
    public void destroy() {
        if (destroyed.compareAndSet(false, true)) {
            shutdown();
        }
    }

    @Override
    public SocketAddress localAddress() {
        return this.address;
    }

    @Override
    public SocketAddress remoteAddress() {
        Channel channel = this.channel;
        if (channel != null) return channel.remoteAddress();
        return null;
    }

    @Override
    public boolean isActive() {
        Channel channel = this.channel;
        if (channel == null) return false;
        return channel.isActive();
    }

    @Override
    public IoBuffer allocate() {
        return this.channel.allocate();
    }

    @Override
    public IoBuffer allocate(int capacity) {
        return this.channel.allocate(capacity);
    }

    public abstract Channel doBind() throws Throwable;

    public abstract void doShutdown(int timeout);
}
