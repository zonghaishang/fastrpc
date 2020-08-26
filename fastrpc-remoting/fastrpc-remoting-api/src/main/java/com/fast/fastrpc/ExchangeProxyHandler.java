package com.fast.fastrpc;

/**
 * @author yiji
 * @version : HeadExchangeHandler.java, v 0.1 2020-08-03
 */
public interface ExchangeProxyHandler extends ChannelHandler {

    ChannelHandler getHandler();

}
