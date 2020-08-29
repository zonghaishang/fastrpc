package com.fast.fastrpc;

/**
 * @author yiji
 * @version : ExchangeProxyHandler.java, v 0.1 2020-08-03
 */
public interface ExchangeDelegateHandler extends ExchangeHandler {

    ChannelHandler getHandler();

}
