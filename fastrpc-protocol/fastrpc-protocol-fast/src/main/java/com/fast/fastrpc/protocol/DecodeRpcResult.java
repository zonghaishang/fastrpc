package com.fast.fastrpc.protocol;

import com.fast.fastrpc.Decoder;
import com.fast.fastrpc.Invocation;
import com.fast.fastrpc.RpcResult;
import com.fast.fastrpc.channel.AttributeKey;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.channel.DefaultFuture;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.exchange.Request;
import com.fast.fastrpc.exchange.Response;
import com.fast.fastrpc.common.buffer.IoBuffer;
import com.fast.fastrpc.serialize.ObjectInput;
import com.fast.fastrpc.serialize.Serialization;
import com.fast.fastrpc.serialize.SerializationCodec;
import com.fast.fastrpc.serialize.SimpleMapSerialization;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import static com.fast.fastrpc.exchange.Response.CLIENT_ERROR;
import static com.fast.fastrpc.protocol.codec.AbstractFastCodec.EXCEPTION;
import static com.fast.fastrpc.protocol.codec.AbstractFastCodec.NORMAL;
import static com.fast.fastrpc.protocol.codec.AbstractFastCodec.NULL;
import static com.fast.fastrpc.protocol.codec.AbstractFastCodec.attachmentIndex;
import static com.fast.fastrpc.protocol.codec.AbstractFastCodec.headerIndex;

/**
 * @author yiji
 * @version : DecodeRpcResult.java, v 0.1 2020-08-20
 */
public class DecodeRpcResult extends RpcResult implements Decoder {

    protected Logger logger = LoggerFactory.getLogger(DecodeRpcResult.class);

    private Channel channel;

    private Response response;

    private IoBuffer buffer;

    private AtomicBoolean decoded = new AtomicBoolean();

    private boolean decodeInIo;

    final static AttributeKey<Map<Integer, DefaultFuture>> futureKey = AttributeKey.valueOf("futures");

    private static final SimpleMapSerialization mapSerialization = new SimpleMapSerialization();

    public DecodeRpcResult(Channel channel, Response response, IoBuffer buffer, boolean decodeInIo) {
        this.channel = channel;
        this.response = response;
        this.buffer = buffer;
        this.decodeInIo = decodeInIo;
        if (decodeInIo) {
            decode();
        }
    }

    @Override
    public void decode() {
        if (decoded.compareAndSet(false, true)) {
            try {

                int serializeId = response.getSerializeId();
                Serialization serialization = SerializationCodec.getSerialization(serializeId);
                ObjectInput input = serialization.deserialize(this.channel.getUrl(), this.buffer);

                int len = this.buffer.getShort(headerIndex);
                this.buffer.readerIndex(attachmentIndex);

                if (len > 0) {
                    setAttachments(mapSerialization.decodeAttachment(this.buffer, len));
                }

                byte flag = input.readByte();

                switch (flag) {
                    /*
                     * returns a null value.
                     */
                    case NULL: {
                        break;
                    }
                    /*
                     * returns the exception value
                     */
                    case EXCEPTION: {
                        setException((Throwable) input.readObject());
                        break;
                    }
                    case NORMAL: {
                        Map<Integer, DefaultFuture> futures = channel.attr(futureKey).get();
                        DefaultFuture future = futures.get(response.getId());

                        if (future == null || future.getRequest() == null) {
                            /*
                             * request maybe timeout already, quickly skip the entire message length.
                             * therefore, there is no need to waste CPU decoding.
                             */
                            setValue(null);
                            return;
                        }

                        Request request = future.getRequest();
                        Invocation invocation = (Invocation) request.getPayload();
                        setValue(input.readObject(invocation.getReturnType(), invocation.getGenericReturnType()));
                    }
                    default:
                        throw new IOException("unsupported response flag, expect '0' '1' '2', actual " + flag);
                }
            } catch (Throwable e) {
                if (logger.isErrorEnabled()) {
                    logger.error("Failed to decode request : " + e.getMessage(), e);
                }
                response.setStatus(CLIENT_ERROR);
                response.setError(e.getMessage());
            } finally {
                if (!decodeInIo) {
                    this.buffer.release();
                }
            }
        }
    }

}
