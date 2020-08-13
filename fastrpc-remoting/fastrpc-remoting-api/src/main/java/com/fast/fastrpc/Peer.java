package com.fast.fastrpc;

import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.channel.InvokeFuture;
import com.fast.fastrpc.common.Host;

/**
 * @author yiji
 * @version : Peer.java, v 0.1 2020-08-03
 */
public interface Peer extends Host, Channel {

    @Override
    InvokeFuture shutdown(int timeout);

}
