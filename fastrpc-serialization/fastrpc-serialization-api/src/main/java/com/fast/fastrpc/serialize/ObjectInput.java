package com.fast.fastrpc.serialize;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * @author yiji
 * @version : ObjectInput.java, v 0.1 2020-08-17
 */
public interface ObjectInput {

    boolean readBoolean() throws IOException;

    byte readByte() throws IOException;

    short readShort() throws IOException;

    int readInt() throws IOException;

    long readLong() throws IOException;

    char readChar() throws IOException;

    float readFloat() throws IOException;

    double readDouble() throws IOException;

    Object readObject() throws IOException;

    <T> T readObject(Class<T> clazz) throws IOException;

    <T> T readObject(Class<T> clazz, Type type) throws IOException;
}
