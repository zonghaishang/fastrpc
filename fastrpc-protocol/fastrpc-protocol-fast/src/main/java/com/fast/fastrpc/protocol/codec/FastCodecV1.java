package com.fast.fastrpc.protocol.codec;

import com.fast.fastrpc.ProtocolCodec;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.exchange.Request;
import com.fast.fastrpc.exchange.Response;
import com.fast.fastrpc.protocol.DecodeInvocation;
import com.fast.fastrpc.remoting.netty.buffer.IoBuffer;

import java.io.IOException;

/**
 * @author yiji
 * @version : FastCodecV1.java, v 0.1 2020-08-17
 */
public class FastCodecV1 extends AbstractFastCodec implements ProtocolCodec {

    protected Logger logger = LoggerFactory.getLogger(FastCodecV1.class);

    @Override
    protected Request decodeRequest(Channel channel, IoBuffer buffer, int length) throws IOException {

        int id = buffer.getInt(idIndex);
        Request request = new Request(id);

        try {
            byte protocolVersion = buffer.getByte(versionIndex);
            request.setProtocolVersion(protocolVersion);

            byte flag = buffer.getByte(flagIndex);
            int requestType = flag >> requestTypeOffset;
            request.setOneWay((requestType & ONEWAY) != 0);

            /**
             * We received the heartbeat request packet.
             */
            if ((flag & heartbeatMask) != 0) {
                request.setHeartbeat(true);
                return request;
            }

            /**
             * We received the ready only request packet(server maybe offline).
             */
            if ((flag & readonlyMask) != 0) {
                request.setReadOnly(true);
                return request;
            }

            request.setCompress((byte) (flag & compressMask));

            byte serializeId = buffer.getByte(codecIndex);
            request.setSerializeId(serializeId);

            /**
             * 1. If decode in IO threads, we can avoid memory copy.
             * 2. Allows decoding in the user thread pool.
             */
            boolean decodeInIo = channel.getUrl().getParameter(Constants.DECODE_IN_KEY, true);
            DecodeInvocation invocation;
            if (decodeInIo) {
                invocation = new DecodeInvocation(channel, request, buffer, true);
                invocation.decode();
            } else {
                // we need to wrap buffer.
                invocation = new DecodeInvocation(channel, request, buffer.copy(buffer.readerIndex(), length), false);
            }
            request.setPayload(invocation);
        } catch (Throwable e) {
            logger.warn("Failed to decode request.", e);
            request.setBroken(true);
            // ?? wrap decode exception ?
            request.setPayload(e);
        }

        return request;
    }

    @Override
    protected Response decodeResponse(Channel channel, IoBuffer buffer, int length) throws IOException {
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