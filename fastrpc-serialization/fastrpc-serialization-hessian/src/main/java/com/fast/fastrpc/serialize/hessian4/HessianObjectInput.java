package com.fast.fastrpc.serialize.hessian4;

import com.caucho.hessian.io.Hessian2Input;
import com.fast.fastrpc.common.buffer.IoBuffer;
import com.fast.fastrpc.common.buffer.IoBufferInputStream;
import com.fast.fastrpc.serialize.ObjectInput;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author yiji
 * @version : HessianObjectInput.java, v 0.1 2020-08-23
 */
public class HessianObjectInput implements ObjectInput {

    private final Hessian2Input input;

    public HessianObjectInput(IoBuffer buffer) {
        this.input = new Hessian2Input(new IoBufferInputStream(buffer));
    }

    @Override
    public boolean readBoolean() throws IOException {
        return this.input.readBoolean();
    }

    @Override
    public byte readByte() throws IOException {
        return (byte) this.input.readInt();
    }

    @Override
    public short readShort() throws IOException {
        return (short) this.input.readInt();
    }

    @Override
    public int readInt() throws IOException {
        return this.input.readInt();
    }

    @Override
    public long readLong() throws IOException {
        return this.input.readLong();
    }

    @Override
    public char readChar() throws IOException {
        return (char) this.input.readInt();
    }

    @Override
    public float readFloat() throws IOException {
        return (float) this.input.readDouble();
    }

    @Override
    public double readDouble() throws IOException {
        return this.input.readDouble();
    }

    @Override
    public String readUTF() throws IOException {
        return this.input.readString();
    }

    @Override
    public byte[] readBytes() throws IOException {
        return this.input.readBytes();
    }

    @Override
    public Object readObject() throws IOException {
        return this.input.readObject();
    }

    @Override
    public <T> T readObject(Class<T> clazz) throws IOException {
        return (T) this.input.readObject(clazz);
    }

    @Override
    public <T> T readObject(Class<T> clazz, Type genericType) throws IOException {
        return (T) this.input.readObject(clazz);
    }
}
