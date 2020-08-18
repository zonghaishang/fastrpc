package com.fast.fastrpc.serialize;

import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.remoting.netty.buffer.IoBuffer;

import java.io.IOException;

/**
 * @author yiji
 * @version : Serialization.java, v 0.1 2020-08-17
 */
public interface Serialization {

    int getContentId();

    String getName();

    ObjectOutput serialize(URL url, IoBuffer buffer) throws IOException;

    ObjectInput deserialize(URL url, IoBuffer buffer) throws IOException;

}
