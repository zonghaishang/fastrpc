package com.fast.fastrpc.remoting.netty.channel;

import com.fast.fastrpc.Timeout;
import com.fast.fastrpc.TimeoutException;
import com.fast.fastrpc.channel.Attribute;
import com.fast.fastrpc.channel.AttributeKey;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.channel.ChannelPromise;
import com.fast.fastrpc.channel.DefaultChannelFuture;
import com.fast.fastrpc.channel.DefaultFuture;
import com.fast.fastrpc.channel.InvokeFuture;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.remoting.netty.TimeoutTask;
import com.fast.fastrpc.remoting.netty.Timer;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;

import java.net.SocketAddress;

/**
 * @author yiji
 * @version : NettyChannel.java, v 0.1 2020-08-06
 */
public class NettyChannel implements Channel {

    private URL url;

    private io.netty.channel.Channel channel;

    private static io.netty.util.AttributeKey<NettyChannel> channelKey = io.netty.util.AttributeKey.valueOf("channelKey");

    public NettyChannel(io.netty.channel.Channel channel, URL url) {
        if (this.channel == null) throw new IllegalArgumentException("channel is required.");
        if (this.url == null) throw new IllegalArgumentException("url is required.");
        this.channel = channel;
        this.url = url;
    }

    @Override
    public boolean isActive() {
        return this.channel.isActive();
    }

    @Override
    public void write(Object message, final ChannelPromise promise) {
        channel.writeAndFlush(message).addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(final ChannelFuture future) {
                if (promise != null) {
                    promise.complete(new DefaultChannelFuture(future.isSuccess(), future.cause()));
                }
            }
        });
    }

    @Override
    public InvokeFuture shutdown() {
        return shutdown(0);
    }

    @Override
    public InvokeFuture shutdown(int timeout) {
        final DefaultFuture invokeFuture = new DefaultFuture(this);

        if (timeout > 0) {
            Timeout task = Timer.createTimeout(new TimeoutTask() {
                @Override
                public void execute(Timeout timeout) {
                    if (invokeFuture.isDone()) return;
                    invokeFuture.receive(new TimeoutException(NettyChannel.this, "shutdown channel timeout."));
                }
            }, timeout);
            invokeFuture.setTimeout(task);
        }

        this.channel.close().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (invokeFuture.isDone()) return;
                invokeFuture.cancelTimeout();
                invokeFuture.receive(future.isSuccess() ? DefaultFuture.SUCCESS : future.cause());
            }
        });

        return invokeFuture;
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        Attribute<T> attribute = key.getAttribute();
        if (attribute != null) return attribute;

        io.netty.util.AttributeKey<T> internalKey = io.netty.util.AttributeKey.valueOf(key.getName());
        attribute = new DefaultAttribute<>(key, this.channel.attr(internalKey));
        key.setAttribute(attribute);

        return attribute;
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {
        if (key.getAttribute() != null) return true;
        io.netty.util.AttributeKey<T> internalKey = io.netty.util.AttributeKey.valueOf(key.getName());
        return this.channel.hasAttr(internalKey);
    }

    public static NettyChannel getOrAddChannel(io.netty.channel.Channel channel, URL url) {
        NettyChannel nettyChannel = channel.attr(channelKey).get();
        if (nettyChannel == null) {
            nettyChannel = new NettyChannel(channel, url);
            channel.attr(channelKey).setIfAbsent(nettyChannel);
        }
        return nettyChannel;
    }

    public static NettyChannel removeChannel(io.netty.channel.Channel channel) {
        return channel.attr(channelKey).getAndSet(null);
    }

    @Override
    public SocketAddress localAddress() {
        return this.channel.localAddress();
    }

    @Override
    public SocketAddress remoteAddress() {
        return this.channel.remoteAddress();
    }


    @Override
    public URL getUrl() {
        return this.url;
    }


    @Override
    public String toString() {
        return localAddress() + " -> " + remoteAddress();
    }

    public io.netty.channel.Channel getChannel() {
        return channel;
    }
}
