package com.fast.fastrpc.remoting.netty;

import com.fast.fastrpc.ChannelHandler;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.Server;
import com.fast.fastrpc.channel.Attribute;
import com.fast.fastrpc.channel.AttributeKey;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.PrefixThreadFactory;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.remoting.netty.channel.NettyChannel;
import com.fast.fastrpc.transporter.AbstractServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yiji
 * @version : NettyServer.java, v 0.1 2020-08-06
 */
public class NettyServer extends AbstractServer implements Server {

    private ServerBootstrap bootstrap;

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private Map<Channel, Channel> channels = new ConcurrentHashMap<>();

    public NettyServer(URL url, ChannelHandler handler) throws RemotingException {
        super(url, handler);
    }

    @Override
    public Channel doBind() throws Throwable {
        bootstrap = new ServerBootstrap();

        bossGroup = new NioEventLoopGroup(1, new PrefixThreadFactory("NettyServerBoss"));
        workerGroup = new NioEventLoopGroup(getUrl().getParameter(Constants.WORKER_THREADS_KEY, Constants.DEFAULT_WORKER_THREADS),
                new PrefixThreadFactory("NettyServerWorker"));

        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childOption(ChannelOption.TCP_NODELAY, Boolean.TRUE)
                .childOption(ChannelOption.SO_REUSEADDR, Boolean.TRUE)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        NettyCodec adapter = new NettyCodec(getUrl(), getCodec());
                        NettyHandler handler = new NettyHandler(getUrl(), NettyServer.this);
                        ch.pipeline()
                                .addLast("decoder", adapter.getDecoder())
                                .addLast("encoder", adapter.getEncoder())
                                .addLast("handler", handler);
                    }
                });

        ChannelFuture channelFuture = bootstrap.bind();
        channelFuture.syncUninterruptibly();
        channel = new NettyChannel(channelFuture.channel());
        return channel;
    }

    @Override
    public void connected(Channel channel) throws RemotingException {
        super.connected(channel);
        channels.put(channel, channel);
    }

    @Override
    public void disconnected(Channel channel) throws RemotingException {
        super.disconnected(channel);
        channels.remove(channel);
    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return this.channel.attr(key);
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {
        return this.channel.hasAttr(key);
    }

    @Override
    public Collection<Channel> getChannels() {
        return this.channels.values();
    }

    @Override
    public void doShutdown(int timeout) {

    }
}
