package com.fast.fastrpc;

import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.spi.ExtensionLoader;
import com.fast.fastrpc.exchange.Exchanger;

/**
 * @author yiji
 * @version : Exchangers.java, v 0.1 2020-08-25
 */
public class Exchangers {

    private Exchangers() {
    }

    public static Client connect(URL url, ExchangeHandler handler) throws RemotingException {
        if (url == null) {
            throw new IllegalArgumentException("url is required.");
        }
        if (handler == null) {
            throw new IllegalArgumentException("handler is required.");
        }

        return getExchanger(url).connect(url, handler);
    }

    public static Server bind(URL url, ExchangeHandler handler) throws RemotingException {
        if (url == null) {
            throw new IllegalArgumentException("url is required.");
        }
        if (handler == null) {
            throw new IllegalArgumentException("handler is required.");
        }

        return getExchanger(url).bind(url, handler);
    }

    private static Exchanger getExchanger(URL url) {
        String name = url.getParameter(Constants.EXCHANGER_KEY, Constants.HEADER);
        return ExtensionLoader.getExtensionLoader(Exchanger.class).getExtension(name);
    }


}
