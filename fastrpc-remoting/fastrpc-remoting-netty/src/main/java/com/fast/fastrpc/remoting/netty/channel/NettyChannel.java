package com.fast.fastrpc.remoting.netty.channel;

import com.fast.fastrpc.DefaultFuture;
import com.fast.fastrpc.InvokeFuture;
import com.fast.fastrpc.Timeout;
import com.fast.fastrpc.channel.Attribute;
import com.fast.fastrpc.channel.AttributeKey;
import com.fast.fastrpc.channel.Channel;
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

    private io.netty.channel.Channel channel;

    public NettyChannel(io.netty.channel.Channel channel) {
        if (this.channel == null) throw new IllegalArgumentException("channel is required.");
        this.channel = channel;
    }

    @Override
    public boolean isActive() {
        return this.channel.isActive();
    }

    @Override
    public InvokeFuture write(Object msg, int timeout) {
        return null;
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
                    invokeFuture.receive(DefaultFuture.TIMEOUT);
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

    @Override
    public SocketAddress localAddress() {
        return this.channel.localAddress();
    }

    @Override
    public SocketAddress remoteAddress() {
        return this.channel.remoteAddress();
    }
}