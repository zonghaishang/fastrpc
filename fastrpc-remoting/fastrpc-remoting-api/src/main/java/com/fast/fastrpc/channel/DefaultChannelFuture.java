package com.fast.fastrpc.channel;

/**
 * @author yiji
 * @version : DefaultChannelFuture.java, v 0.1 2020-08-13
 */
public class DefaultChannelFuture implements ChannelFuture{

    private boolean success;
    private Throwable cause;

    public DefaultChannelFuture(boolean success, Throwable cause) {
        this.success = success;
        this.cause = cause;
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public Throwable cause() {
        return cause;
    }
}
