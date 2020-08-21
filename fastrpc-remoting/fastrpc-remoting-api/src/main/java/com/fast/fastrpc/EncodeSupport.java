package com.fast.fastrpc;

import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.buffer.IoBuffer;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.common.spi.ExtensionLoader;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yiji
 * @version : AbstractExchange.java, v 0.1 2020-08-21
 */
public abstract class EncodeSupport implements Encoder {

    protected Logger logger = LoggerFactory.getLogger(getClass());

    private IoBuffer buffer; // encoded buffer

    private AtomicBoolean encoded = new AtomicBoolean();

    public void encode(Channel channel, IoBuffer buffer) throws IOException {
        if (encoded.compareAndSet(false, true)) {

            if (buffer == null) {
                buffer = channel.allocate();
                this.buffer = buffer;
            }

            try {
                String name = channel.getUrl().getParameter(Constants.CODEC_KEY, channel.getUrl().getProtocol());
                Codec codec = ExtensionLoader.getExtensionLoader(Codec.class).getExtension(name);
                codec.encode(channel, buffer, this);
            } catch (Throwable e) {
                if (logger.isWarnEnabled()) {
                    logger.warn("Failed to encode object, type :" + getClass().getName() + " message : " + e.getMessage(), e);
                }
                throw new IOException("Failed to encode object, type : " + getClass().getName(), e);
            }
        }
    }

    public IoBuffer getBuffer() {
        return buffer;
    }

}
