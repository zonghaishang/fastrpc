package com.fast.fastrpc.remoting.netty;

import com.fast.fastrpc.Codec;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.remoting.netty.buffer.Buffer;
import com.fast.fastrpc.remoting.netty.buffer.IoBuffer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.MessageToByteEncoder;

import java.io.IOException;
import java.util.List;

import static com.fast.fastrpc.remoting.netty.channel.NettyChannel.getOrAddChannel;

/**
 * @author yiji
 * @version : NettyCodecAdapter.java, v 0.1 2020-08-07
 */
public class NettyCodec {

    private URL url;

    private Codec codec;

    private ProtocolEncoder encoder = new ProtocolEncoder();
    private ProtocolDecoder decoder = new ProtocolDecoder();

    public NettyCodec(URL url, Codec codec) {
        this.url = url;
        this.codec = codec;
    }

    public ProtocolEncoder getEncoder() {
        return encoder;
    }

    public ProtocolDecoder getDecoder() {
        return decoder;
    }

    private class ProtocolEncoder extends MessageToByteEncoder {
        @Override
        protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf buf) throws Exception {
            codec.encode(getOrAddChannel(ctx.channel(), url), Buffer.wrap(buf), msg);
        }
    }

    private class ProtocolDecoder extends ByteToMessageDecoder {
        @Override
        protected void decode(ChannelHandlerContext ctx, ByteBuf input, List<Object> out) throws Exception {
            IoBuffer buffer = Buffer.wrap(input);
            Object decoded;

            while (buffer.readableBytes() > 0) {

                try {
                    buffer.markReaderIndex();
                    decoded = codec.decode(getOrAddChannel(ctx.channel(), url), buffer);
                } catch (IOException e) {
                    throw e;
                }

                /**
                 * The decoded message may not be long enough.
                 * Here we reset the read index and let the protocol
                 * layer focus on the decoding logic.
                 */
                if (decoded == null) {
                    buffer.resetReaderIndex();
                }

                out.add(decoded);
            }

        }
    }
}
