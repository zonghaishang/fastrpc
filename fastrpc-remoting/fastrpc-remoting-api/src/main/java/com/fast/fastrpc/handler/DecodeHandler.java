package com.fast.fastrpc.handler;

import com.fast.fastrpc.ChannelHandler;
import com.fast.fastrpc.Decoder;
import com.fast.fastrpc.ExchangeHandlerAdapter;
import com.fast.fastrpc.RemotingException;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.exchange.Request;
import com.fast.fastrpc.exchange.Response;

/**
 * @author yiji
 * @version : DecodeHandler.java, v 0.1 2020-08-26
 */
public class DecodeHandler extends ExchangeHandlerAdapter {

    private static final Logger logger = LoggerFactory.getLogger(DecodeHandler.class);

    public DecodeHandler(ChannelHandler handler) {
        super(handler);
    }

    @Override
    public void received(Channel channel, Object message) throws RemotingException {

        /*
         * 1. If decode in IO threads, we can avoid memory copy.
         * 2. Allows decoding in the user thread pool.
         */
        boolean decodeInIo = channel.getUrl().getParameter(Constants.CODEC_IN_IO_KEY, true);
        if (!decodeInIo) {

            if (message instanceof Request) {
                decode(((Request) message).getPayload());
            } else if (message instanceof Response) {
                decode(((Response) message).getPayload());
            }

        }

        this.handler.received(channel, message);
    }

    private void decode(Object message) {
        if (message instanceof Decoder) {
            try {
                ((Decoder) message).decode();
            } catch (Throwable e) {
                if (logger.isErrorEnabled()) {
                    logger.error("Failed to decode message, message： " + e.getMessage(), e);
                }
            }
        }
    }
}
