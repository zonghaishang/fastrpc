package com.fast.fastrpc.protocol.v1;

import com.fast.fastrpc.ProtocolCodec;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.exchange.Request;
import com.fast.fastrpc.exchange.Response;
import com.fast.fastrpc.protocol.AbstractFastCodec;
import com.fast.fastrpc.remoting.netty.buffer.IoBuffer;

import java.io.IOException;

/**
 * @author yiji
 * @version : FastCodecV1.java, v 0.1 2020-08-17
 */
public class FastCodecV1 extends AbstractFastCodec implements ProtocolCodec {

    @Override
    protected Request decodeRequest(Channel channel, IoBuffer buffer) throws IOException {
        return null;
    }

    @Override
    protected Response decodeResponse(Channel channel, IoBuffer buffer) throws IOException {
        return null;
    }

    @Override
    protected void encodeRequest(Channel channel, IoBuffer buffer, Request request) throws IOException {

    }

    @Override
    protected void encodeResponse(Channel channel, IoBuffer buffer, Response response) throws IOException {

    }

    @Override
    public int getVersion() {
        return VERSION_1;
    }
}