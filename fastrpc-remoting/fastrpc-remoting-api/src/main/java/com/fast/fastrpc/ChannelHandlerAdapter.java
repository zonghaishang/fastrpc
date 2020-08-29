package com.fast.fastrpc;

import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.channel.InvokeFuture;

/**
 * @author yiji
 * @version : AbstractDelegateHandler.java, v 0.1 2020-08-29
 */
public abstract class ChannelHandlerAdapter implements ExchangeDelegateHandler {

    @Override
    public ChannelHandler getHandler() {
        return null;
    }

    @Override
    public void connected(Channel channel) throws RemotingException {

    }

    @Override
    public void disconnected(Channel channel) throws RemotingException {

    }

    @Override
    public InvokeFuture write(Channel channel, Object message) throws RemotingException {
        return null;
    }

    @Override
    public void received(Channel channel, Object message) throws RemotingException {

    }

    @Override
    public void caught(Channel channel, Throwable exception) throws RemotingException {

    }

    @Override
    public Object reply(Channel channel, Object message) throws RemotingException {
        return null;
    }
}
