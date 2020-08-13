package com.fast.fastrpc;

import com.fast.fastrpc.channel.Channel;

import java.util.Collection;
import java.util.List;

/**
 * @author yiji
 * @version : Server.java, v 0.1 2020-08-03
 */
public interface Server extends Peer {

    void start() throws RemotingException;

    Collection<Channel> getChannels();

}
