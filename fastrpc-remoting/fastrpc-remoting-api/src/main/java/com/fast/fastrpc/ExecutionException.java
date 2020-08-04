package com.fast.fastrpc;

import com.fast.fastrpc.channel.Channel;

import java.net.SocketAddress;

/**
 * @author yiji
 * @version : ExecutionException.java, v 0.1 2020-08-05
 */
public class ExecutionException extends RemotingException {

    private Object source;

    public ExecutionException(Channel channel, String msg) {
        super(channel, msg);
    }

    public ExecutionException(SocketAddress localAddress, SocketAddress remoteAddress, String message) {
        super(localAddress, remoteAddress, message);
    }

    public ExecutionException(Channel channel, Throwable cause) {
        super(channel, cause);
    }

    public ExecutionException(SocketAddress localAddress, SocketAddress remoteAddress, Throwable cause) {
        super(localAddress, remoteAddress, cause);
    }

    public ExecutionException(Channel channel, String message, Throwable cause) {
        super(channel, message, cause);
    }

    public ExecutionException(Object source, Channel channel, String message, Throwable cause) {
        super(channel, message, cause);
        this.source = source;
    }

    public ExecutionException(SocketAddress localAddress, SocketAddress remoteAddress, String message, Throwable cause) {
        super(localAddress, remoteAddress, message, cause);
    }

    public Object getSource() {
        return source;
    }
}
