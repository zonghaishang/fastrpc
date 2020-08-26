package com.fast.fastrpc.exchange;

import com.fast.fastrpc.Client;
import com.fast.fastrpc.ExchangeHandler;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.Server;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.spi.SPI;

/**
 * @author yiji
 * @version : Exchanger.java, v 0.1 2020-08-25
 */
@SPI("header")
public interface Exchanger {

    Server bind(URL url, ExchangeHandler handler) throws RemotingException;

    Client connect(URL url, ExchangeHandler handler) throws RemotingException;

}
