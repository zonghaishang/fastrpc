package com.fast.fastrpc.remoting.netty;

import com.fast.fastrpc.ChannelHandler;
import com.fast.fastrpc.InvokeFuture;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.channel.Attribute;
import com.fast.fastrpc.channel.AttributeKey;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.transporter.AbstractServer;

import java.util.List;

/**
 * @author yiji
 * @version : NettyServer.java, v 0.1 2020-08-06
 */
public class NettyServer extends AbstractServer {

    public NettyServer(URL url, ChannelHandler handler) throws RemotingException {
        super(url, handler);
    }

    @Override
    public Channel doBind() throws Throwable {
        return null;
    }

    @Override
    public InvokeFuture doShutdown(int timeout) {
        return null;
    }

    @Override
    public void connected(Channel channel) throws RemotingException {

    }

    @Override
    public void disconnected(Channel channel) throws RemotingException {

    }

    @Override
    public void write(Channel channel, Object message) throws RemotingException {

    }

    @Override
    public void received(Channel channel, Object message) throws RemotingException {

    }

    @Override
    public void caught(Channel channel, Throwable exception) throws RemotingException {

    }

    @Override
    public List<Channel> getChannels() {
        return null;
    }

    @Override
    public InvokeFuture write(Object msg, int timeout) {
        return null;
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return null;
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {
        return false;
    }
}
