package com.fast.fastrpc;

import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.buffer.IoBuffer;

import java.io.IOException;

/**
 * @author yiji
 * @version : Encoder.java, v 0.1 2020-08-20
 */
public interface Encoder {

    void encode(Channel channel, IoBuffer buffer) throws IOException;

    /**
     * get encoded buffer.
     *
     * @return encoded buffer.
     */
    IoBuffer getBuffer();

}
