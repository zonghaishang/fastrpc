package com.fast.fastrpc.transporter;

import com.fast.fastrpc.ChannelHandler;
import com.fast.fastrpc.Client;
import com.fast.fastrpc.InvokeFuture;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.PrefixThreadFactory;
import com.fast.fastrpc.common.URL;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * @author yiji
 * @version : AbstractClient.java, v 0.1 2020-08-05
 */
public abstract class AbstractClient extends AbstractPeer implements Client {

    protected volatile Channel channel;

    protected InetSocketAddress remoteAddress;

    protected int interval;

    protected volatile ScheduledFuture<?> future;

    protected static final ScheduledThreadPoolExecutor scheduledPool
            = new ScheduledThreadPoolExecutor(2, new PrefixThreadFactory("reconnect"));

    public AbstractClient(URL url, ChannelHandler handler) throws RemotingException {
        super(url, handler);
        this.interval = url.getParameter(Constants.RECONNECT_KEY, 3000);
        // ready to connect to server.
        start();
    }

    protected void start() throws RemotingException {
        try {
            this.remoteAddress = new InetSocketAddress(getUrl().getHost(), getUrl().getPort());
            doOpen();

            this.channel = doConnect();
            if (logger.isInfoEnabled()) {
                logger.info("Success connect to server on " + this.remoteAddress);
            }
        } catch (Throwable e) {
            throw new RemotingException(null, this.remoteAddress, "Failed to connect to server on " + this.remoteAddress);
        } finally {
            // TCP reconnection is enabled by default
            scheduleReconnectIfRequired();
        }
    }

    @Override
    public void connect() throws RemotingException {
        try {
            Channel channel = this.channel;
            if (channel == null) {
                this.channel = doConnect();
                return;
            }
            if (channel.isActive()) return;

            shutdown();
            this.channel = doConnect();
        } catch (Throwable e) {
            throw new RemotingException(this.channel, "Failed to connect to server " + this.remoteAddress, e);
        } finally {
            scheduleReconnectIfRequired();
        }
    }

    private void scheduleReconnectIfRequired() {
        if (this.interval > 0 && (this.future == null || this.future.isCancelled())) {
            this.future = scheduledPool.scheduleAtFixedRate(new Runnable() {
                @Override
                public void run() {
                    try {
                        connect();
                    } catch (RemotingException e) {
                        logger.warn("Failed to reconnect to server on " + AbstractClient.this.remoteAddress, e);
                    }
                }
            }, this.interval, this.interval, TimeUnit.MILLISECONDS);
        }
    }

    @Override
    public InvokeFuture shutdown() {
        return shutdown(this.shutdownTimeout);
    }

    @Override
    public InvokeFuture shutdown(int timeout) {
        InvokeFuture future = doShutdown(timeout);
        Channel channel = this.channel;
        if (channel != null) channel.shutdown();
        return future;
    }

    @Override
    public void destroy() {

        /**
         * Terminate scheduling task(may be reconnecting).
         */
        ScheduledFuture<?> future = this.future;
        if (future != null) {
            future.cancel(true);
        }

        shutdown();
    }

    @Override
    public SocketAddress localAddress() {
        Channel channel = this.channel;
        if (channel != null) return channel.localAddress();
        return null;
    }

    @Override
    public SocketAddress remoteAddress() {
        return this.remoteAddress;
    }

    @Override
    public boolean isActive() {
        Channel channel = this.channel;
        if (channel == null) return false;
        return channel.isActive();
    }

    public abstract void doOpen() throws Throwable;

    public abstract Channel doConnect() throws RemotingException;

    public abstract InvokeFuture doShutdown(int timeout);
}
