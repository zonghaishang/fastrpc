package com.fast.fastrpc.common.buffer;

import java.nio.charset.Charset;

/**
 * @author yiji
 * @version : IoBuffer.java, v 0.1 2020-08-03
 */
public interface IoBuffer {

    /**
     * Returns the number of bytes (octets) this buffer can contain.
     */
    int capacity();

    /**
     * Returns {@code true} if and only if this buffer is backed by an
     * NIO direct buffer.
     */
    boolean isDirect();

    /**
     * Returns the {@code readerIndex} of this buffer.
     */
    int readerIndex();

    /**
     * Sets the {@code readerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code readerIndex} is
     *                                   less than {@code 0} or
     *                                   greater than {@code this.writerIndex}
     */
    IoBuffer readerIndex(int readerIndex);

    /**
     * Returns the {@code writerIndex} of this buffer.
     */
    int writerIndex();

    /**
     * Sets the {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code writerIndex} is
     *                                   less than {@code this.readerIndex} or
     *                                   greater than {@code this.capacity}
     */
    IoBuffer writerIndex(int writerIndex);

    /**
     * Returns the number of readable bytes which is equal to
     * {@code (this.writerIndex - this.readerIndex)}.
     */
    int readableBytes();

    /**
     * Returns the number of writable bytes which is equal to
     * {@code (this.capacity - this.writerIndex)}.
     */
    int writableBytes();

    /**
     * Sets the {@code readerIndex} and {@code writerIndex} of this buffer to
     * {@code 0}.
     */
    IoBuffer clear();

    /**
     * Marks the current {@code readerIndex} in this buffer.  You can
     * reposition the current {@code readerIndex} to the marked
     * {@code readerIndex} by calling {@link #resetReaderIndex()}.
     * The initial value of the marked {@code readerIndex} is {@code 0}.
     */
    IoBuffer markReaderIndex();

    /**
     * Repositions the current {@code readerIndex} to the marked
     * {@code readerIndex} in this buffer.
     *
     * @throws IndexOutOfBoundsException if the current {@code writerIndex} is less than the marked
     *                                   {@code readerIndex}
     */
    IoBuffer resetReaderIndex();

    /**
     * Marks the current {@code writerIndex} in this buffer.  You can
     * reposition the current {@code writerIndex} to the marked
     * {@code writerIndex} by calling {@link #resetWriterIndex()}.
     * The initial value of the marked {@code writerIndex} is {@code 0}.
     */
    IoBuffer markWriterIndex();

    /**
     * Repositions the current {@code writerIndex} to the marked
     * {@code writerIndex} in this buffer.
     *
     * @throws IndexOutOfBoundsException if the current {@code readerIndex} is greater than the marked
     *                                   {@code writerIndex}
     */
    IoBuffer resetWriterIndex();

    /**
     * Gets a boolean at the specified absolute (@code index) in this buffer.
     * This method does not modify the {@code readerIndex} or {@code writerIndex}
     * of this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 1} is greater than {@code this.capacity}
     */
    boolean getBoolean(int index);

    /**
     * Gets a byte at the specified absolute {@code index} in this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 1} is greater than {@code this.capacity}
     */
    byte getByte(int index);

    /**
     * Gets a 16-bit short integer at the specified absolute {@code index} in
     * this buffer.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 2} is greater than {@code this.capacity}
     */
    short getShort(int index);

    /**
     * Gets a 24-bit medium integer at the specified absolute {@code index} in
     * this buffer.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 3} is greater than {@code this.capacity}
     */
    int getMedium(int index);

    /**
     * Gets a 32-bit integer at the specified absolute {@code index} in
     * this buffer.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 4} is greater than {@code this.capacity}
     */
    int getInt(int index);

    /**
     * Gets a 64-bit long integer at the specified absolute {@code index} in
     * this buffer.  This method does not modify {@code readerIndex} or
     * {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 8} is greater than {@code this.capacity}
     */
    long getLong(int index);

    CharSequence getCharSequence(int index, int length);

    CharSequence getCharSequence(int index, int length, Charset charset);

    /**
     * Gets a 2-byte UTF-16 character at the specified absolute
     * {@code index} in this buffer.  This method does not modify
     * {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 2} is greater than {@code this.capacity}
     */
    char getChar(int index);

    /**
     * Gets a 32-bit floating point number at the specified absolute
     * {@code index} in this buffer.  This method does not modify
     * {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 4} is greater than {@code this.capacity}
     */
    float getFloat(int index);

    /**
     * Gets a 64-bit floating point number at the specified absolute
     * {@code index} in this buffer.  This method does not modify
     * {@code readerIndex} or {@code writerIndex} of this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 8} is greater than {@code this.capacity}
     */
    double getDouble(int index);

    /**
     * Transfers this buffer's data to the specified destination starting at
     * the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   if {@code index + dst.length} is greater than
     *                                   {@code this.capacity}
     */
    IoBuffer getBytes(int index, byte[] dst);

    /**
     * Sets the specified boolean at the specified absolute {@code index} in this
     * buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 1} is greater than {@code this.capacity}
     */
    IoBuffer setBoolean(int index, boolean value);

    /**
     * Sets the specified byte at the specified absolute {@code index} in this
     * buffer.  The 24 high-order bits of the specified value are ignored.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 1} is greater than {@code this.capacity}
     */
    IoBuffer setByte(int index, int value);

    /**
     * Sets the specified 16-bit short integer at the specified absolute
     * {@code index} in this buffer.  The 16 high-order bits of the specified
     * value are ignored.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 2} is greater than {@code this.capacity}
     */
    IoBuffer setShort(int index, int value);

    /**
     * Sets the specified 24-bit medium integer at the specified absolute
     * {@code index} in this buffer.  Please note that the most significant
     * byte is ignored in the specified value.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 3} is greater than {@code this.capacity}
     */
    IoBuffer setMedium(int index, int value);

    /**
     * Sets the specified 32-bit integer at the specified absolute
     * {@code index} in this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 4} is greater than {@code this.capacity}
     */
    IoBuffer setInt(int index, int value);

    /**
     * Sets the specified 64-bit long integer at the specified absolute
     * {@code index} in this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 8} is greater than {@code this.capacity}
     */
    IoBuffer setLong(int index, long value);

    /**
     * Sets the specified 2-byte UTF-16 character at the specified absolute
     * {@code index} in this buffer.
     * The 16 high-order bits of the specified value are ignored.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 2} is greater than {@code this.capacity}
     */
    IoBuffer setChar(int index, int value);

    /**
     * Sets the specified 32-bit floating-point number at the specified
     * absolute {@code index} in this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 4} is greater than {@code this.capacity}
     */
    IoBuffer setFloat(int index, float value);

    /**
     * Sets the specified 64-bit floating-point number at the specified
     * absolute {@code index} in this buffer.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   {@code index + 8} is greater than {@code this.capacity}
     */
    IoBuffer setDouble(int index, double value);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0} or
     *                                   if {@code index + src.length} is greater than
     *                                   {@code this.capacity}
     */
    IoBuffer setBytes(int index, byte[] src);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the specified absolute {@code index}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     *
     * @throws IndexOutOfBoundsException if the specified {@code index} is less than {@code 0},
     *                                   if the specified {@code srcIndex} is less than {@code 0},
     *                                   if {@code index + length} is greater than
     *                                   {@code this.capacity}, or
     *                                   if {@code srcIndex + length} is greater than {@code src.length}
     */
    IoBuffer setBytes(int index, byte[] src, int srcIndex, int length);

    /**
     * Gets a boolean at the current {@code readerIndex} and increases
     * the {@code readerIndex} by {@code 1} in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 1}
     */
    boolean readBoolean();

    /**
     * Gets a byte at the current {@code readerIndex} and increases
     * the {@code readerIndex} by {@code 1} in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 1}
     */
    byte readByte();

    /**
     * Gets a 16-bit short integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 2} in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 2}
     */
    short readShort();

    /**
     * Gets a 24-bit medium integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 3} in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 3}
     */
    int readMedium();

    /**
     * Gets a 32-bit integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 4} in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 4}
     */
    int readInt();

    /**
     * Gets a 64-bit integer at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 8} in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 8}
     */
    long readLong();

    CharSequence readCharSequence(int length);

    CharSequence readCharSequence(int length, Charset charset);

    IoBuffer readBytes(byte[] dst);

    IoBuffer readBytes(byte[] dst, int dstIndex, int length);

    /**
     * Gets a 2-byte UTF-16 character at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 2} in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 2}
     */
    char readChar();

    /**
     * Gets a 32-bit floating point number at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 4} in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 4}
     */
    float readFloat();

    /**
     * Gets a 64-bit floating point number at the current {@code readerIndex}
     * and increases the {@code readerIndex} by {@code 8} in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.readableBytes} is less than {@code 8}
     */
    double readDouble();

    /**
     * Returns a new slice of this buffer's sub-region starting at the current
     * {@code readerIndex} and increases the {@code readerIndex} by the size
     * of the new slice (= {@code length}).
     * <p>
     * Also be aware that this method will NOT call {@link #retain()} and so the
     * reference count will NOT be increased.
     *
     * @param length the size of the new slice
     * @return the newly created slice
     * @throws IndexOutOfBoundsException if {@code length} is greater than {@code this.readableBytes}
     */
    IoBuffer readSlice(int length);

    IoBuffer retain();

    IoBuffer touch();

    boolean release();

    /**
     * Increases the current {@code readerIndex} by the specified
     * {@code length} in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code length} is greater than {@code this.readableBytes}
     */
    IoBuffer skipBytes(int length);

    /**
     * Sets the specified boolean at the current {@code writerIndex}
     * and increases the {@code writerIndex} by {@code 1} in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 1}
     */
    IoBuffer writeBoolean(boolean value);

    /**
     * Sets the specified byte at the current {@code writerIndex}
     * and increases the {@code writerIndex} by {@code 1} in this buffer.
     * The 24 high-order bits of the specified value are ignored.
     *
     * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 1}
     */
    IoBuffer writeByte(int value);

    /**
     * Sets the specified 16-bit short integer at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 2}
     * in this buffer.  The 16 high-order bits of the specified value are ignored.
     *
     * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 2}
     */
    IoBuffer writeShort(int value);

    /**
     * Sets the specified 24-bit medium integer at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 3}
     * in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 3}
     */
    IoBuffer writeMedium(int value);

    /**
     * Sets the specified 32-bit integer at the current {@code writerIndex}
     * and increases the {@code writerIndex} by {@code 4} in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 4}
     */
    IoBuffer writeInt(int value);

    /**
     * Sets the specified 64-bit long integer at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 8}
     * in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 8}
     */
    IoBuffer writeLong(long value);

    IoBuffer writeCharSequence(CharSequence sequence);

    IoBuffer writeCharSequence(CharSequence sequence, Charset charset);

    /**
     * Sets the specified 2-byte UTF-16 character at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 2}
     * in this buffer.  The 16 high-order bits of the specified value are ignored.
     *
     * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 2}
     */
    IoBuffer writeChar(int value);

    /**
     * Sets the specified 32-bit floating point number at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 4}
     * in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 4}
     */
    IoBuffer writeFloat(float value);

    /**
     * Sets the specified 64-bit floating point number at the current
     * {@code writerIndex} and increases the {@code writerIndex} by {@code 8}
     * in this buffer.
     *
     * @throws IndexOutOfBoundsException if {@code this.writableBytes} is less than {@code 8}
     */
    IoBuffer writeDouble(double value);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the current {@code writerIndex} and increases the {@code writerIndex}
     * by the number of the transferred bytes (= {@code src.length}).
     *
     * @throws IndexOutOfBoundsException if {@code src.length} is greater than {@code this.writableBytes}
     */
    IoBuffer writeBytes(byte[] src);

    /**
     * Transfers the specified source array's data to this buffer starting at
     * the current {@code writerIndex} and increases the {@code writerIndex}
     * by the number of the transferred bytes (= {@code length}).
     *
     * @param srcIndex the first index of the source
     * @param length   the number of bytes to transfer
     * @throws IndexOutOfBoundsException if the specified {@code srcIndex} is less than {@code 0},
     *                                   if {@code srcIndex + length} is greater than
     *                                   {@code src.length}, or
     *                                   if {@code length} is greater than {@code this.writableBytes}
     */
    IoBuffer writeBytes(byte[] src, int srcIndex, int length);

    /**
     * Returns a slice of this buffer's readable bytes. Modifying the content
     * of the returned buffer or this buffer affects each other's content
     * while they maintain separate indexes and marks.  This method is
     * identical to {@code buf.slice(buf.readerIndex(), buf.readableBytes())}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     * <p>
     * Also be aware that this method will NOT call {@link #retain()} and so the
     * reference count will NOT be increased.
     */
    IoBuffer slice();

    /**
     * Returns a slice of this buffer's sub-region. Modifying the content of
     * the returned buffer or this buffer affects each other's content while
     * they maintain separate indexes and marks.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     * <p>
     * Also be aware that this method will NOT call {@link #retain()} and so the
     * reference count will NOT be increased.
     */
    IoBuffer slice(int index, int length);

    /**
     * Returns a copy of this buffer's readable bytes.  Modifying the content
     * of the returned buffer or this buffer does not affect each other at all.
     * This method is identical to {@code buf.copy(buf.readerIndex(), buf.readableBytes())}.
     * This method does not modify {@code readerIndex} or {@code writerIndex} of
     * this buffer.
     */
    IoBuffer copy();

    IoBuffer copy(int index, int length);

    IoBuffer writeBytes(IoBuffer src);

    IoBuffer writeBytes(IoBuffer src, int length);

    /**
     * Returns {@code true} if and only if this buffer has a backing byte array.
     * If this method returns true, you can safely call {@link #array()} and
     * {@link #arrayOffset()}.
     */
    boolean hasArray();

    /**
     * Returns the backing byte array of this buffer.
     *
     * @throws UnsupportedOperationException if there no accessible backing byte array
     */
    byte[] array();

    /**
     * Returns the offset of the first byte within the backing byte array of
     * this buffer.
     *
     * @throws UnsupportedOperationException if there no accessible backing byte array
     */
    int arrayOffset();

    <T> T unwrap();
}
