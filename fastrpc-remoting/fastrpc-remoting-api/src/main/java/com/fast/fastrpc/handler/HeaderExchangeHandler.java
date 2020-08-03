package com.fast.fastrpc.handler;

import com.fast.fastrpc.channel.AttributeKey;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.ExchangeHandler;
import com.fast.fastrpc.ExchangeProxyHandler;
import com.fast.fastrpc.RemotingException;

/**
 * @author yiji
 * @version : HeaderExchangeHandler.java, v 0.1 2020-08-03
 */
public class HeaderExchangeHandler implements ExchangeProxyHandler {

    static final AttributeKey<Long> readTimestamp = AttributeKey.valueOf("readTimestamp");
    static final AttributeKey<Long> writeTimestamp = AttributeKey.valueOf("writeTimestamp");

    private ExchangeHandler handler;

    public HeaderExchangeHandler(ExchangeHandler handler) {
        if (handler == null) throw new IllegalArgumentException("handler is required.");
        this.handler = handler;
    }

    @Override
    public void connected(Channel channel) throws RemotingException {
        updateTimestamp(channel);
        handler.connected(channel);
    }

    @Override
    public void disconnected(Channel channel) throws RemotingException {
        updateTimestamp(channel);
        handler.disconnected(channel);
    }

    @Override
    public void sent(Channel channel, Object message) throws RemotingException {
        updateWriteTimestamp(channel);
    }

    @Override
    public void received(Channel channel, Object message) throws RemotingException {
        updateReadTimestamp(channel);
        // todo 处理请求request、response
    }

    @Override
    public void caught(Channel channel, Throwable exception) throws RemotingException {

    }

    @Override
    public ExchangeHandler getHandler() {
        return this.handler;
    }

    private void updateTimestamp(Channel channel) {
        long now = System.currentTimeMillis();
        channel.attr(readTimestamp).set(now);
        channel.attr(writeTimestamp).set(now);
    }

    private void updateReadTimestamp(Channel channel) {
        channel.attr(readTimestamp).set(System.currentTimeMillis());
    }

    private void updateWriteTimestamp(Channel channel) {
        channel.attr(writeTimestamp).set(System.currentTimeMillis());
    }
}
