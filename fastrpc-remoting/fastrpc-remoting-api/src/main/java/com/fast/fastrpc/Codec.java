package com.fast.fastrpc;

import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.spi.SPI;
import com.fast.fastrpc.common.buffer.IoBuffer;

import java.io.IOException;

/**
 * @author yiji
 * @version : Codec.java, v 0.1 2020-08-05
 */
@SPI
public interface Codec {

    void encode(Channel channel, IoBuffer buffer, Object message) throws IOException;

    Object decode(Channel channel, IoBuffer buffer) throws IOException;

}
