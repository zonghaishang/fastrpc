package com.fast.fastrpc.remoting.netty;

import com.fast.fastrpc.ChannelHandler;
import com.fast.fastrpc.Client;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.Server;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.transporter.Transporter;

/**
 * @author yiji
 * @version : NettyTransport.java, v 0.1 2020-08-06
 */
public class NettyTransporter implements Transporter {

    @Override
    public Server bind(URL url, ChannelHandler handler) throws RemotingException {
        return new NettyServer(url, handler);
    }

    @Override
    public Client connect(URL url, ChannelHandler handler) throws RemotingException {
        return new NettyClient(url, handler);
    }
}
