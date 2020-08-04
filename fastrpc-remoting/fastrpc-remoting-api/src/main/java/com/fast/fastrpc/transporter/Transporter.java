package com.fast.fastrpc.transporter;

import com.fast.fastrpc.ChannelHandler;
import com.fast.fastrpc.Client;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.Server;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.spi.SPI;

/**
 * @author yiji
 * @version : Transporter.java, v 0.1 2020-08-04
 */
@SPI("netty")
public interface Transporter {

    Server bind(URL url, ChannelHandler handler) throws RemotingException;

    Client connect(URL url, ChannelHandler handler) throws RemotingException;

}
