package com.fast.fastrpc.protocol;

import com.fast.fastrpc.AbstractCodec;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.exchange.Request;
import com.fast.fastrpc.exchange.Response;
import com.fast.fastrpc.remoting.netty.buffer.IoBuffer;

import java.io.IOException;

/**
 * @author yiji
 * @version : FastCodec.java, v 0.1 2020-08-17
 */
public abstract class AbstractFastCodec extends AbstractCodec {

    protected static final int RESPONSE = 0;
    protected static final int REQUEST = 1;
    protected static final int ONEWAY = 2;

    protected static final int VERSION_1 = 1;

    @Override
    protected Object decodePayload(Channel channel, IoBuffer buffer) throws IOException {

        int readable = buffer.readableBytes();
        if (readable < HEADER_LENGTH) return null;

        int header = buffer.getShort(10);
        int payload = buffer.getInt(12);
        int length = HEADER_LENGTH + header + payload;
        if (readable < length) return null;

        byte flag = buffer.getByte(2);
        int requestType = flag >> 6;

        switch (requestType) {
            case RESPONSE: {
                return decodeResponse(channel, buffer);
            }
            case REQUEST:
            case ONEWAY: {
                return decodeRequest(channel, buffer);
            }
            default: {
                throw new IOException("unsupported rpc request type '" + requestType + "'");
            }
        }
    }

    protected abstract Request decodeRequest(Channel channel, IoBuffer buffer) throws IOException;

    protected abstract Response decodeResponse(Channel channel, IoBuffer buffer) throws IOException;

}
