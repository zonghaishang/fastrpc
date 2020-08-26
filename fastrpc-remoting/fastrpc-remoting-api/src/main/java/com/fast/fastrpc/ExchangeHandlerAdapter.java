package com.fast.fastrpc;

import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.channel.InvokeFuture;

/**
 * @author yiji
 * @version : ExchangeHandlerAdapter.java, v 0.1 2020-08-26
 */
public class ExchangeHandlerAdapter implements ExchangeProxyHandler {

    protected ChannelHandler handler;

    public ExchangeHandlerAdapter(ChannelHandler handler) {
        this.handler = handler;
    }

    @Override
    public ChannelHandler getHandler() {
        if (handler instanceof ExchangeProxyHandler) {
            return ((ExchangeProxyHandler) handler).getHandler();
        }
        return handler;
    }

    @Override
    public void connected(Channel channel) throws RemotingException {
        this.handler.connected(channel);
    }

    @Override
    public void disconnected(Channel channel) throws RemotingException {
        this.handler.disconnected(channel);
    }

    @Override
    public InvokeFuture write(Channel channel, Object message) throws RemotingException {
        return this.handler.write(channel, message);
    }

    @Override
    public void received(Channel channel, Object message) throws RemotingException {
        this.handler.received(channel, message);
    }

    @Override
    public void caught(Channel channel, Throwable exception) throws RemotingException {
        this.handler.caught(channel, exception);
    }
}
