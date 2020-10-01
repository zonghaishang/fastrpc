package com.fast.fastrpc.remoting.netty;

import com.fast.fastrpc.ChannelHandler;
import com.fast.fastrpc.Client;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.channel.Attribute;
import com.fast.fastrpc.channel.AttributeKey;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.PrefixThreadFactory;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.common.utils.ConfUtils;
import com.fast.fastrpc.common.utils.NetUtils;
import com.fast.fastrpc.remoting.netty.channel.NettyChannel;
import com.fast.fastrpc.transporter.AbstractClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yiji
 * @version : NettyClient.java, v 0.1 2020-08-06
 */
public class NettyClient extends AbstractClient implements Client {

    protected final static Logger logger = LoggerFactory.getLogger(NettyClient.class);

    private static NioEventLoopGroup workerGroup;
    private Bootstrap bootstrap;

    private final static AtomicBoolean workerPrepared = new AtomicBoolean();

    public NettyClient(URL url, ChannelHandler handler) throws RemotingException {
        super(url, handler);
    }

    @Override
    public void doOpen() {
        prepareWorkerGroup();
        bootstrap = new Bootstrap();

        bootstrap.group(workerGroup)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .option(ChannelOption.TCP_NODELAY, true)
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .channel(NioSocketChannel.class);

        bootstrap.handler(new ChannelInitializer() {
            @Override
            protected void initChannel(io.netty.channel.Channel ch) throws Exception {
                NettyCodec adapter = new NettyCodec(getUrl(), getCodec());
                NettyHandler handler = new NettyHandler(getUrl(), NettyClient.this);
                ch.pipeline()
                        .addLast("decoder", adapter.getDecoder())
                        .addLast("encoder", adapter.getEncoder())
                        .addLast("handler", handler);
            }
        });
    }

    @Override
    public Channel doConnect() throws RemotingException {

        if (isActive()) return this.channel;

        long start = System.currentTimeMillis();
        ChannelFuture future = bootstrap.connect(remoteAddress());

        boolean completed = future.awaitUninterruptibly(getConnectTimeout(), TimeUnit.MILLISECONDS);
        if (completed && future.isSuccess()) {
            Channel oldChannel = this.channel;

            // Close an existing channel first.
            if (oldChannel != null) {
                if (logger.isInfoEnabled()) {
                    logger.info("Close old netty channel " + oldChannel);
                }
                oldChannel.shutdown();
            }

            if (isDestroyed()) {
                // Close connected channel if we found client is destroyed.
                future.channel().close();
                throw new RemotingException(this, "Close connected netty channel " + future.channel() + ", because the client destroyed already.");
            }
        }

        if (future.cause() != null) {
            throw new RemotingException(this, "Failed to connect to server "
                    + remoteAddress() + ", error message is:" + future.cause().getMessage(), future.cause());
        }

        if (!completed) {
            throw new RemotingException(this, "Failed to connect to server "
                    + remoteAddress() + " client side timeout "
                    + getConnectTimeout() + "ms (elapsed: " + (System.currentTimeMillis() - start) + "ms) from client "
                    + NetUtils.getLocalHost());
        }

        this.channel = new NettyChannel(future.channel(), getUrl());
        return this.channel;
    }

    private void prepareWorkerGroup() {
        if (workerPrepared.get()) return;
        synchronized (workerPrepared) {
            String workers = getUrl().getParameter(Constants.WORKER_THREADS_KEY, ConfUtils.getProperty(Constants.WORKER_THREADS_KEY));
            int nThreads = Constants.DEFAULT_WORKER_THREADS;
            if (workers != null && (workers = workers.trim()).length() > 0) {
                try {
                    nThreads = Integer.parseInt(workers);
                } catch (Throwable e) {
                    logger.warn("Failed to parse worker threads", e);
                }
            }
            workerGroup = new NioEventLoopGroup(nThreads, new PrefixThreadFactory("NettyClientWorker"));
            workerPrepared.set(true);
        }
    }

    @Override
    public void doShutdown(int timeout) {

    }

    @Override
    public <T> Attribute<T> attr(AttributeKey<T> key) {
        return this.channel.attr(key);
    }

    @Override
    public <T> boolean hasAttr(AttributeKey<T> key) {
        return this.channel.hasAttr(key);
    }
}
