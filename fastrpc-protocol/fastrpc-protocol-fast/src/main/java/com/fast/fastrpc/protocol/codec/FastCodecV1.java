package com.fast.fastrpc.protocol.codec;

import com.fast.fastrpc.Invocation;
import com.fast.fastrpc.ProtocolCodec;
import com.fast.fastrpc.Result;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.buffer.IoBuffer;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.common.utils.ReflectUtils;
import com.fast.fastrpc.exchange.Request;
import com.fast.fastrpc.exchange.Response;
import com.fast.fastrpc.protocol.DecodeInvocation;
import com.fast.fastrpc.protocol.DecodeRpcResult;
import com.fast.fastrpc.serialize.ObjectOutput;
import com.fast.fastrpc.serialize.Serialization;
import com.fast.fastrpc.serialize.SerializationCodec;
import com.fast.fastrpc.serialize.SimpleMapSerialization;

import java.io.IOException;

/**
 * @author yiji
 * @version : FastCodecV1.java, v 0.1 2020-08-17
 */
public class FastCodecV1 extends AbstractFastCodec implements ProtocolCodec {

    protected Logger logger = LoggerFactory.getLogger(FastCodecV1.class);

    private static final SimpleMapSerialization mapSerialization = new SimpleMapSerialization();

    @Override
    protected Request decodeRequest(Channel channel, IoBuffer buffer, int length) {

        int id = buffer.getInt(idIndex);
        Request request = new Request(id);

        try {
            byte protocolVersion = buffer.getByte(versionIndex);
            request.setProtocolVersion(protocolVersion);

            byte flag = buffer.getByte(flagIndex);
            int requestType = flag >> requestTypeOffset;
            request.setOneWay((requestType & ONEWAY) != 0);
            request.setCompress((byte) (flag & compressMask));

            byte serializeId = buffer.getByte(codecIndex);
            request.setSerializeId(serializeId);

            short timeout = buffer.getShort(timeoutIndex);
            request.setTimeout(timeout);

            if ((flag & heartbeatMask) != 0) {
                request.setHeartbeat(true);
                return request;
            }

            if ((flag & readonlyMask) != 0) {
                request.setReadOnly(true);
                return request;
            }

            /*
             * 1. If decode in IO threads, we can avoid memory copy.
             * 2. Allows decoding in the user thread pool.
             */
            boolean decodeInIo = channel.getUrl().getParameter(Constants.CODEC_IN_IO_KEY, true);
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
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to decode request.", e);
            }
            request.setBroken(true);
            // ?? wrap decode exception ?
            request.setPayload(e);
        }

        return request;
    }

    @Override
    protected Response decodeResponse(Channel channel, IoBuffer buffer, int length) {

        int id = buffer.getInt(idIndex);
        Response response = new Response(id);

        try {
            byte protocolVersion = buffer.getByte(versionIndex);
            response.setProtocolVersion(protocolVersion);

            byte flag = buffer.getByte(flagIndex);
            response.setCompress((byte) (flag & compressMask));

            byte serializeId = buffer.getByte(codecIndex);
            response.setSerializeId(serializeId);

            short status = buffer.getShort(statusIndex);
            response.setStatus(status);

            if ((flag & heartbeatMask) != 0) {
                response.setHeartbeat(true);
                return response;
            }

            if ((flag & readonlyMask) != 0) {
                response.setReadOnly(true);
                return response;
            }

            /*
             * 1. If decode in IO threads, we can avoid memory copy.
             * 2. Allows decoding in the user thread pool.
             */
            boolean decodeInIo = channel.getUrl().getParameter(Constants.CODEC_IN_IO_KEY, true);
            DecodeRpcResult rpcResult;
            if (decodeInIo) {
                rpcResult = new DecodeRpcResult(channel, response, buffer, true);
                rpcResult.decode();
            } else {
                // we need to wrap buffer.
                rpcResult = new DecodeRpcResult(channel, response, buffer.copy(buffer.readerIndex(), length), false);
            }
            response.setPayload(rpcResult);
        } catch (Throwable e) {
            if (logger.isWarnEnabled()) {
                logger.warn("Failed to decode request.", e);
            }
            // ?? wrap decode exception ?
            response.setPayload(e);
        }

        return response;
    }

    @Override
    protected void encodeRequest(Channel channel, IoBuffer buffer, Request request) throws IOException {

        IoBuffer ioBuffer = request.getBuffer();

        /*
         * 1. user thread has been encoded already, write directly to the buffer.
         * 2. release the encoded IOBuffer
         */
        if (ioBuffer != null) {
            buffer.writeBytes(ioBuffer);
            ioBuffer.release();
        } else {

            int offset = buffer.readerIndex();

            buffer.writeByte(magic);
            buffer.writeByte(VERSION_1);
            buffer.writeByte(requestFlag);

            Serialization serialization = SerializationCodec.getSerialization(channel.getUrl());
            buffer.writeByte(serialization.getContentId());
            buffer.writeInt(request.getId());
            buffer.writeShort(request.getTimeout());

            Invocation invocation = (Invocation) request.getPayload();

            buffer.readerIndex(offset + attachmentIndex);
            int header = mapSerialization.encodeAttachment(buffer, invocation.getAttachments());
            // update header length
            buffer.setShort(offset + headerIndex, header);

            ObjectOutput out = serialization.serialize(channel.getUrl(), buffer);
            int readerIndex = buffer.readerIndex();

            // write parameter descriptor
            buffer.writeCharSequence(ReflectUtils.getDesc(invocation.getParameterTypes()));
            Object[] arguments = invocation.getArguments();
            if (arguments != null) {
                for (Object argument : arguments) {
                    out.writeObject(argument);
                }
            }
            // update payload length
            buffer.setInt(offset + payloadIndex, buffer.readerIndex() - readerIndex);
            out.flush();
        }

    }

    @Override
    protected void encodeResponse(Channel channel, IoBuffer buffer, Response response) throws IOException {

        IoBuffer ioBuffer = response.getBuffer();

        /*
         * 1. user thread has been encoded already, write directly to the buffer.
         * 2. release the encoded IOBuffer
         */
        if (ioBuffer != null) {
            buffer.writeBytes(ioBuffer);
            ioBuffer.release();
        } else {

            int offset = buffer.readerIndex();

            buffer.writeByte(magic);
            buffer.writeByte(VERSION_1);
            buffer.writeByte(responseFlag);

            Serialization serialization = SerializationCodec.getSerialization(channel.getUrl());
            buffer.writeByte(serialization.getContentId());
            buffer.writeInt(response.getId());
            buffer.writeShort(response.getStatus());

            Result result = (Result) response.getPayload();

            buffer.readerIndex(offset + attachmentIndex);
            int header = mapSerialization.encodeAttachment(buffer, result.getAttachments());
            // update header length
            buffer.setShort(offset + headerIndex, header);

            ObjectOutput out = serialization.serialize(channel.getUrl(), buffer);
            int readerIndex = buffer.readerIndex();

            Throwable error = result.getException();
            if (error != null) {
                out.writeByte(EXCEPTION);
                out.writeObject(error);
            } else {
                Object value = result.getValue();
                if (value != null) {
                    out.writeByte(NORMAL);
                    out.writeObject(value);
                } else {
                    out.writeByte(NULL);
                }
            }

            // update payload length
            buffer.setInt(offset + payloadIndex, buffer.readerIndex() - readerIndex);
            out.flush();
        }

    }

    @Override
    public int getVersion() {
        return VERSION_1;
    }
}