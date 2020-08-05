package com.fast.fastrpc.transporter;

import com.fast.fastrpc.ChannelHandler;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.Server;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.URL;

import java.net.InetSocketAddress;

/**
 * @author yiji
 * @version : AbstractServer.java, v 0.1 2020-08-05
 */
public abstract class AbstractServer extends AbstractPeer implements Server {

    protected InetSocketAddress address;

    protected volatile Channel channel;

    public AbstractServer(URL url, ChannelHandler handler) throws RemotingException {
        super(url, handler);
        // ready to start server.
        start();
    }

    @Override
    public void start() throws RemotingException {
        this.address = new InetSocketAddress(getUrl().getHost(), getUrl().getPort());
        try {
            this.channel = doBind();
            if (logger.isInfoEnabled()) {
                logger.info("success to start server on " + this.address);
            }
        } catch (Throwable e) {
            throw new RemotingException(this.channel, "Failed to start server.");
        }
    }

    public abstract Channel doBind() throws Throwable;
}
