package com.fast.fastrpc.common.buffer;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author yiji
 * @version : IoBufferInputStream.java, v 0.1 2020-08-23
 */
public class IoBufferInputStream extends InputStream {

    private final IoBuffer buffer;
    private final int startIndex;
    private final int endIndex;

    public IoBufferInputStream(IoBuffer buffer) {
        this(buffer, buffer.readableBytes());
    }

    public IoBufferInputStream(IoBuffer buffer, int length) {
        if (length < 0) {
            throw new IllegalArgumentException("length: " + length);
        }
        if (length > buffer.readableBytes()) {
            throw new IndexOutOfBoundsException("Too many bytes to be read - Needs "
                    + length + ", maximum is " + buffer.readableBytes());
        }

        this.buffer = buffer;
        startIndex = buffer.readerIndex();
        endIndex = startIndex + length;
        buffer.markReaderIndex();
    }

    public int readBytes() {
        return buffer.readerIndex() - startIndex;
    }

    @Override
    public int available() {
        return endIndex - buffer.readerIndex();
    }

    @Override
    public void mark(int limit) {
        buffer.markReaderIndex();
    }

    @Override
    public boolean markSupported() {
        return true;
    }

    @Override
    public int read() throws IOException {
        int available = available();
        if (available == 0) {
            return -1;
        }
        return buffer.readByte() & 0xff;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int available = available();
        if (available == 0) {
            return -1;
        }

        len = Math.min(available, len);
        buffer.readBytes(b, off, len);
        return len;
    }

    @Override
    public void reset() {
        buffer.resetReaderIndex();
    }

    @Override
    public long skip(long n) throws IOException {
        if (n > Integer.MAX_VALUE) {
            return skipBytes(Integer.MAX_VALUE);
        } else {
            return skipBytes((int) n);
        }
    }

    public int skipBytes(int n) {
        int nBytes = Math.min(available(), n);
        buffer.skipBytes(nBytes);
        return nBytes;
    }
}
