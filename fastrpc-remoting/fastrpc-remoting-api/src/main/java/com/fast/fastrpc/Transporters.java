package com.fast.fastrpc;

import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.spi.ExtensionLoader;
import com.fast.fastrpc.transporter.Transporter;

/**
 * @author yiji
 * @version : Transporters.java, v 0.1 2020-08-26
 */
public class Transporters {

    private Transporters() {
    }

    public static Client connect(URL url, ChannelHandler handler) throws RemotingException {
        if (url == null) {
            throw new IllegalArgumentException("url is required.");
        }
        if (handler == null) {
            throw new IllegalArgumentException("handler is required.");
        }

        return getTransporter(url).connect(url, handler);
    }

    public static Server bind(URL url, ChannelHandler handler) throws RemotingException {
        if (url == null) {
            throw new IllegalArgumentException("url is required.");
        }
        if (handler == null) {
            throw new IllegalArgumentException("handler is required.");
        }

        return getTransporter(url).bind(url, handler);
    }

    private static Transporter getTransporter(URL url) {
        String name = url.getParameter(Constants.TRANSPORTER_KEY, Constants.NETTY);
        return ExtensionLoader.getExtensionLoader(Transporter.class).getExtension(name);
    }

}
