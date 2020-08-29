package com.fast.fastrpc;

import com.fast.fastrpc.channel.Channel;

/**
 * @author yiji
 * @version : ExchangeHandler.java, v 0.1 2020-08-03
 */
public interface ExchangeHandler extends ChannelHandler {

    Object reply(Channel channel, Object message) throws RemotingException;

}
