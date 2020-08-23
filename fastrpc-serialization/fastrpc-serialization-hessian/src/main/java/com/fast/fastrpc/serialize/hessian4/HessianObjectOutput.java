package com.fast.fastrpc.serialize.hessian4;

import com.caucho.hessian.io.Hessian2Output;
import com.fast.fastrpc.common.buffer.IoBuffer;
import com.fast.fastrpc.common.buffer.IoBufferOutputStream;
import com.fast.fastrpc.serialize.ObjectOutput;

import java.io.IOException;

/**
 * @author yiji
 * @version : HessianObjectOutput.java, v 0.1 2020-08-23
 */
public class HessianObjectOutput implements ObjectOutput {

    private final Hessian2Output output;

    public HessianObjectOutput(IoBuffer buffer) {
        this.output = new Hessian2Output(new IoBufferOutputStream(buffer));
    }

    @Override
    public void writeBoolean(boolean value) throws IOException {
        this.output.writeBoolean(value);
    }

    @Override
    public void writeByte(int value) throws IOException {
        this.output.writeInt(value);
    }

    @Override
    public void writeShort(int value) throws IOException {
        this.output.writeInt(value);
    }

    @Override
    public void writeInt(int value) throws IOException {
        this.output.writeInt(value);
    }

    @Override
    public void writeLong(long value) throws IOException {
        this.output.writeLong(value);
    }

    @Override
    public void writeChar(int value) throws IOException {
        this.output.writeInt(value);
    }

    @Override
    public void writeFloat(float value) throws IOException {
        this.output.writeDouble(value);
    }

    @Override
    public void writeDouble(double value) throws IOException {
        this.output.writeDouble(value);
    }

    @Override
    public void writeString(String value) throws IOException {
        this.output.writeString(value);
    }

    @Override
    public void writeBytes(byte[] value) throws IOException {
       this.output.writeBytes(value);
    }

    @Override
    public void writeBytes(byte[] value, int offset, int len) throws IOException {
        this.output.writeBytes(value, offset, len);
    }

    @Override
    public void writeObject(Object value) throws IOException {
        this.output.writeObject(value);
    }

    @Override
    public void flush() throws IOException {
        this.output.flush();
    }
}
