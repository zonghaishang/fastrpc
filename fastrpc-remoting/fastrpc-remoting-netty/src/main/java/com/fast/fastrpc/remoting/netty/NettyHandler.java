package com.fast.fastrpc.remoting.netty;

import com.fast.fastrpc.ChannelHandler;
import com.fast.fastrpc.common.URL;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;

import static com.fast.fastrpc.remoting.netty.channel.NettyChannel.getOrAddChannel;
import static com.fast.fastrpc.remoting.netty.channel.NettyChannel.removeChannel;

/**
 * @author yiji
 * @version : NettyHandlerAdapter.java, v 0.1 2020-08-07
 */
public class NettyHandler extends ChannelDuplexHandler {

    private URL url;

    private ChannelHandler handler;

    public NettyHandler(URL url, ChannelHandler handler) {
        this.url = url;
        this.handler = handler;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.handler.connected(getOrAddChannel(ctx.channel(), url));
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
        this.handler.disconnected(removeChannel(ctx.channel()));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
        this.handler.received(getOrAddChannel(ctx.channel(), url), msg);
    }

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        super.write(ctx, msg, promise);
        this.handler.write(getOrAddChannel(ctx.channel(), url), msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        super.exceptionCaught(ctx, cause);
        this.handler.caught(getOrAddChannel(ctx.channel(), url), cause);
    }
}
