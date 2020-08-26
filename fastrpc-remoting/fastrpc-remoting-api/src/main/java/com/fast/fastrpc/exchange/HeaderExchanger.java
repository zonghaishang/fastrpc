package com.fast.fastrpc.exchange;

import com.fast.fastrpc.Client;
import com.fast.fastrpc.ExchangeHandler;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.Server;
import com.fast.fastrpc.Transporters;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.handler.DecodeHandler;
import com.fast.fastrpc.handler.EncodeHandler;
import com.fast.fastrpc.handler.HeaderExchangeHandler;

/**
 * @author yiji
 * @version : HeaderExchanger.java, v 0.1 2020-08-25
 */
public class HeaderExchanger implements Exchanger {

    @Override
    public Server bind(URL url, ExchangeHandler handler) throws RemotingException {
        return Transporters.bind(url, new DecodeHandler(new EncodeHandler(new HeaderExchangeHandler(handler))));
    }

    @Override
    public Client connect(URL url, ExchangeHandler handler) throws RemotingException {
        return Transporters.connect(url, new DecodeHandler(new EncodeHandler(new HeaderExchangeHandler(handler))));
    }

}
