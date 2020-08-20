package com.fast.fastrpc.protocol.codec;

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

    public static final int RESPONSE = 0;
    public static final int REQUEST = 1;
    public static final int ONEWAY = 2;

    public static final int VERSION_1 = 1;

    public static final int magicIndex = 0;
    public static final int versionIndex = 1;
    public static final int flagIndex = 2;
    public static final int codecIndex = 3;
    public static final int idIndex = 4;
    public static final int requestTypeOffset = 6;
    public static final int timeoutIndex = 8;
    public static final int statusIndex = 8;
    public static final int headerIndex = 10;
    public static final int payloadIndex = 12;
    public static final int attachmentIndex = 16;

    public static final byte heartbeatMask = 0x20;
    public static final byte readonlyMask = 0x10;
    public static final byte compressMask = 0x0f;

    public static final byte NULL = 0;
    public static final byte EXCEPTION = 1;
    public static final byte NORMAL = 2;

    public static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    public static final Class<?>[] EMPTY_CLASS_ARRAY = new Class<?>[0];

    @Override
    public Object decode(Channel channel, IoBuffer buffer) throws IOException {

        int readable = buffer.readableBytes();
        if (readable < HEADER_LENGTH) return null;

        int header = buffer.getShort(headerIndex);
        int payload = buffer.getInt(payloadIndex);
        int length = HEADER_LENGTH + header + payload;
        if (readable < length) return null;

        byte flag = buffer.getByte(flagIndex);
        int requestType = flag >> requestTypeOffset;

        try {
            switch (requestType) {
                case RESPONSE: {
                    return decodeResponse(channel, buffer, length);
                }
                case REQUEST:
                case ONEWAY: {
                    return decodeRequest(channel, buffer, length);
                }
                default: {
                    throw new IOException("unsupported rpc request type '" + requestType + "'");
                }
            }
        } finally {
            /**
             * Discard the data that has been read.
             */
            buffer.skipBytes(length);
        }
    }

    protected abstract Request decodeRequest(Channel channel, IoBuffer buffer, int length) throws IOException;

    protected abstract Response decodeResponse(Channel channel, IoBuffer buffer, int length) throws IOException;

}
