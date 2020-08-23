package com.fast.fastrpc.serialize;

import java.io.IOException;

/**
 * @author yiji
 * @version : ObjectOutput.java, v 0.1 2020-08-17
 */
public interface ObjectOutput {

    void writeBoolean(boolean value) throws IOException;

    void writeByte(int value) throws IOException;

    void writeShort(int value) throws IOException;

    void writeInt(int value) throws IOException;

    void writeLong(long value) throws IOException;

    void writeChar(int value) throws IOException;

    void writeFloat(float value) throws IOException;

    void writeDouble(double value) throws IOException;

    void writeString(String value) throws IOException;

    void writeBytes(byte[] value) throws IOException;

    void writeBytes(byte[] value, int offset, int len) throws IOException;

    void writeObject(Object obj) throws IOException;

    void flush() throws IOException;

}
