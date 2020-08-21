package com.fast.fastrpc.protocol;

import com.fast.fastrpc.Decoder;
import com.fast.fastrpc.RpcInvocation;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.common.utils.ReflectUtils;
import com.fast.fastrpc.exchange.Request;
import com.fast.fastrpc.common.buffer.IoBuffer;
import com.fast.fastrpc.serialize.ObjectInput;
import com.fast.fastrpc.serialize.Serialization;
import com.fast.fastrpc.serialize.SerializationCodec;
import com.fast.fastrpc.serialize.SimpleMapSerialization;

import java.util.concurrent.atomic.AtomicBoolean;

import static com.fast.fastrpc.protocol.codec.AbstractFastCodec.EMPTY_CLASS_ARRAY;
import static com.fast.fastrpc.protocol.codec.AbstractFastCodec.EMPTY_OBJECT_ARRAY;
import static com.fast.fastrpc.protocol.codec.AbstractFastCodec.attachmentIndex;
import static com.fast.fastrpc.protocol.codec.AbstractFastCodec.headerIndex;

/**
 * @author yiji
 * @version : DecodeInvocation.java, v 0.1 2020-08-18
 */
public class DecodeInvocation extends RpcInvocation implements Decoder {

    protected Logger logger = LoggerFactory.getLogger(DecodeInvocation.class);

    private Channel channel;

    private Request request;

    private IoBuffer buffer;

    private AtomicBoolean decoded = new AtomicBoolean();

    private boolean decodeInIo;

    private static final SimpleMapSerialization mapSerialization = new SimpleMapSerialization();

    public DecodeInvocation(Channel channel, Request request, IoBuffer buffer, boolean decodeInIo) {
        this.channel = channel;
        this.request = request;
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

                int serializeId = request.getSerializeId();
                Serialization serialization = SerializationCodec.getSerialization(serializeId);
                ObjectInput input = serialization.deserialize(this.channel.getUrl(), this.buffer);

                int len = this.buffer.getShort(headerIndex);
                this.buffer.readerIndex(attachmentIndex);
                setAttachments(mapSerialization.decodeAttachment(this.buffer, len));

                setMethodName(getAttachment(Constants.METHOD_KEY));

                String desc = input.readUTF();
                if (desc == null || desc.length() == 0) {
                    setParameterTypes(EMPTY_CLASS_ARRAY);
                    setArguments(EMPTY_OBJECT_ARRAY);
                    return;
                }

                Class<?>[] parameterTypes = ReflectUtils.desc2classArray(desc);
                Object[] arguments = new Object[parameterTypes.length];
                for (int i = 0; i < arguments.length; i++) {
                    try {
                        arguments[i] = input.readObject(parameterTypes[i]);
                    } catch (Exception e) {
                        if (logger.isErrorEnabled()) {
                            logger.error("Failed to decode argument : " + e.getMessage(), e);
                        }
                        throw e;
                    }
                }

                setParameterTypes(parameterTypes);
                setArguments(arguments);

            } catch (Throwable e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Failed to decode request : " + e.getMessage(), e);
                }
                request.setBroken(true);
                request.setPayload(e);
            } finally {
                if (!decodeInIo) {
                    this.buffer.release();
                }
            }
        }
    }
}
