package com.fast.fastrpc.transporter;

import com.fast.fastrpc.ChannelHandler;
import com.fast.fastrpc.DefaultFuture;
import com.fast.fastrpc.InvokeFuture;
import com.fast.fastrpc.Peer;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yiji
 * @version : AbstractPeer.java, v 0.1 2020-08-04
 */
public abstract class AbstractPeer implements Peer, ChannelHandler {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    protected AtomicBoolean closed = new AtomicBoolean();

    private URL url;
    private ChannelHandler handler;

    public AbstractPeer(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    @Override
    public InvokeFuture shutdown(int timeout) {
        if (closed.get()) return new DefaultFuture();
        return new DefaultFuture();
    }

    @Override
    public InvokeFuture shutdown() {
        return shutdown(-1);
    }
}
