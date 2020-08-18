package com.fast.fastrpc.protocol;

import com.fast.fastrpc.Decoder;
import com.fast.fastrpc.RpcInvocation;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.exchange.Request;
import com.fast.fastrpc.remoting.netty.buffer.IoBuffer;
import com.fast.fastrpc.serialize.ObjectInput;
import com.fast.fastrpc.serialize.Serialization;
import com.fast.fastrpc.serialize.SerializationCodec;

import java.util.concurrent.atomic.AtomicBoolean;

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


            } catch (Throwable e) {
                logger.warn("Failed to decode request.", e);
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
