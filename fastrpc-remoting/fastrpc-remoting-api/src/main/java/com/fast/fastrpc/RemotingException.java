package com.fast.fastrpc;

import com.fast.fastrpc.channel.Channel;

import java.net.SocketAddress;

/**
 * @author yiji
 * @version : RemotingException.java, v 0.1 2020-08-03
 */
public class RemotingException extends Exception {

    private SocketAddress localAddress;
    private SocketAddress remoteAddress;

    public RemotingException(Channel channel, String msg) {
        this(channel == null ? null : channel.localAddress(), channel == null ? null : channel.remoteAddress(),
                msg);
    }

    public RemotingException(SocketAddress localAddress, SocketAddress remoteAddress, String message) {
        super(message);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public RemotingException(Channel channel, Throwable cause) {
        this(channel == null ? null : channel.localAddress(), channel == null ? null : channel.remoteAddress(),
                cause);
    }

    public RemotingException(SocketAddress localAddress, SocketAddress remoteAddress, Throwable cause) {
        this(localAddress, remoteAddress, null, cause);
    }

    public RemotingException(Channel channel, String message, Throwable cause) {
        this(channel == null ? null : channel.localAddress(), channel == null ? null : channel.remoteAddress(),
                message, cause);
    }

    public RemotingException(SocketAddress localAddress, SocketAddress remoteAddress, String message,
                             Throwable cause) {
        super(message, cause);

        this.localAddress = localAddress;
        this.remoteAddress = remoteAddress;
    }

    public SocketAddress getLocalAddress() {
        return localAddress;
    }

    public SocketAddress getRemoteAddress() {
        return remoteAddress;
    }
}