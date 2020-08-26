package com.fast.fastrpc.handler;

import com.fast.fastrpc.ChannelHandler;
import com.fast.fastrpc.Encoder;
import com.fast.fastrpc.ExchangeHandlerAdapter;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.channel.InvokeFuture;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.exchange.Request;
import com.fast.fastrpc.exchange.Response;

/**
 * @author yiji
 * @version : EncodeHandler.java, v 0.1 2020-08-26
 */
public class EncodeHandler extends ExchangeHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(EncodeHandler.class);

    public EncodeHandler(ChannelHandler handler) {
        super(handler);
    }

    @Override
    public InvokeFuture write(Channel channel, Object message) throws RemotingException {

        /*
         * 1. If decode in IO threads, we can avoid memory copy.
         * 2. Allows decoding in the user thread pool.
         */
        boolean decodeInIo = channel.getUrl().getParameter(Constants.CODEC_IN_IO_KEY, true);
        if (!decodeInIo) {

            if (message instanceof Request) {
                encode(channel, ((Request) message).getPayload());
            } else if (message instanceof Response) {
                encode(channel, ((Response) message).getPayload());
            }

        }

        return this.handler.write(channel, message);
    }

    private void encode(Channel channel, Object message) {
        if (message instanceof Encoder) {
            try {
                ((Encoder) message).encode(channel, null);
            } catch (Throwable e) {
                if (logger.isErrorEnabled()) {
                    logger.error("Failed to encode message, message： " + e.getMessage(), e);
                }
            }
        }
    }

}
