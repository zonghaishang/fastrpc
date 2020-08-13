package com.fast.fastrpc.transporter;

import com.fast.fastrpc.ChannelHandler;
import com.fast.fastrpc.channel.ChannelPromise;
import com.fast.fastrpc.channel.InvokeFuture;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.Server;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.URL;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author yiji
 * @version : AbstractServer.java, v 0.1 2020-08-05
 */
public abstract class AbstractServer extends AbstractPeer implements Server {

    protected InetSocketAddress address;

    protected volatile Channel channel;

    public AbstractServer(URL url, ChannelHandler handler) throws RemotingException {
        super(url, handler);
        // ready to start server.
        start();
    }

    @Override
    public void start() throws RemotingException {
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

    @Override
    public InvokeFuture write(Object msg, ChannelPromise promise) throws RemotingException {
        return this.handler.write(this.channel, msg);
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
        shutdown();
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

    public abstract Channel doBind() throws Throwable;

    public abstract void doShutdown(int timeout);
}
