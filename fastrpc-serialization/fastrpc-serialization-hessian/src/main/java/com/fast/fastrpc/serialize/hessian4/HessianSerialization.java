package com.fast.fastrpc.serialize.hessian4;


import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.buffer.IoBuffer;
import com.fast.fastrpc.serialize.ObjectInput;
import com.fast.fastrpc.serialize.ObjectOutput;
import com.fast.fastrpc.serialize.Serialization;

import java.io.IOException;

/**
 * @author yiji
 * @version : HessianSerialization.java, v 0.1 2020-08-23
 */
public class HessianSerialization implements Serialization {

    public static final int HESSIAN4_ID = 0;

    @Override
    public int getContentId() {
        return HESSIAN4_ID;
    }

    @Override
    public String getName() {
        return "hessian4";
    }

    @Override
    public ObjectOutput serialize(URL url, IoBuffer buffer) throws IOException {
        return new HessianObjectOutput(buffer);
    }

    @Override
    public ObjectInput deserialize(URL url, IoBuffer buffer) throws IOException {
        return new HessianObjectInput(buffer);
    }
}
