package com.fast.fastrpc.channel;

import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.common.URL;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

/**
 * @author yiji
 * @version : Channel.java, v 0.1 2020-08-03
 */
public interface Channel extends AttributeMap {

    /**
     * Return {@code true} if the {@link Channel} is active and so connected.
     */
    boolean isActive();

    /**
     * Returns the local address where this channel is bound to.  The returned
     * {@link SocketAddress} is supposed to be down-cast into more concrete
     * type such as {@link InetSocketAddress} to retrieve the detailed
     * information.
     *
     * @return the local address of this channel.
     * {@code null} if this channel is not bound.
     */
    SocketAddress localAddress();

    /**
     * Returns the remote address where this channel is connected to.  The
     * returned {@link SocketAddress} is supposed to be down-cast into more
     * concrete type such as {@link InetSocketAddress} to retrieve the detailed
     * information.
     *
     * @return the remote address of this channel.
     * {@code null} if this channel is not connected.
     */
    SocketAddress remoteAddress();

    void write(Object msg, ChannelPromise promise) throws RemotingException;

    InvokeFuture shutdown();

    InvokeFuture shutdown(int timeout);

    URL getUrl();
}
