package com.fast.fastrpc;

import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.exchange.Request;
import com.fast.fastrpc.exchange.Response;
import com.fast.fastrpc.remoting.netty.buffer.IoBuffer;

import java.io.IOException;

/**
 * @author yiji
 * @version : AbstractCodec.java, v 0.1 2020-08-17
 */
public abstract class AbstractCodec implements Codec {

    protected static final int HEADER_LENGTH = 16;

    @Override
    public Object decode(Channel channel, IoBuffer buffer) throws IOException {
        return decodePayload(channel, buffer);
    }

    @Override
    public void encode(Channel channel, IoBuffer buffer, Object message) throws IOException {
        if (message instanceof Request) {
            encodeRequest(channel, buffer, (Request) message);
        } else if (message instanceof Response) {
            encodeResponse(channel, buffer, (Response) message);
        }
        throw new IOException("Failed to encode unsupported message type " + message.getClass().getName());
    }

    protected abstract Object decodePayload(Channel channel, IoBuffer buffer) throws IOException;

    protected abstract void encodeRequest(Channel channel, IoBuffer buffer, Request request) throws IOException;

    protected abstract void encodeResponse(Channel channel, IoBuffer buffer, Response response) throws IOException;

}
