package com.fast.fastrpc.common.buffer;

import java.io.IOException;
import java.io.OutputStream;

/**
 * @author yiji
 * @version : IoBufferOutputStream.java, v 0.1 2020-08-23
 */
public class IoBufferOutputStream extends OutputStream {

    private final IoBuffer buffer;
    private final int startIndex;

    public IoBufferOutputStream(IoBuffer buffer) {
        this.buffer = buffer;
        startIndex = buffer.writerIndex();
    }

    public int writtenBytes() {
        return buffer.writerIndex() - startIndex;
    }

    @Override
    public void write(byte[] b, int off, int len) {
        if (len == 0) {
            return;
        }

        buffer.writeBytes(b, off, len);
    }

    @Override
    public void write(byte[] b) throws IOException {
        buffer.writeBytes(b);
    }

    @Override
    public void write(int b) throws IOException {
        buffer.writeByte(b);
    }

    public IoBuffer buffer() {
        return buffer;
    }
}
