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

    void writeUTF(String v) throws IOException;

    void writeBytes(byte[] src) throws IOException;

    void writeBytes(byte[] v, int off, int len) throws IOException;

    void writeObject(Object obj) throws IOException;

    void flush();

}
