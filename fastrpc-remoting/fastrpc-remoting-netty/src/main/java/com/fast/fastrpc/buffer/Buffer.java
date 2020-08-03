package com.fast.fastrpc.buffer;

import io.netty.buffer.ByteBuf;

/**
 * @author yiji
 * @version : PooledBuffer.java, v 0.1 2020-08-03
 */
public class Buffer implements IoBuffer {

    private ByteBuf buffer;

    public Buffer(ByteBuf buffer) {
        this.buffer = buffer;
    }

    @Override
    public int capacity() {
        return this.buffer.capacity();
    }

    @Override
    public boolean isDirect() {
        return this.buffer.isDirect();
    }

    @Override
    public int readerIndex() {
        return this.buffer.readerIndex();
    }

    @Override
    public IoBuffer readerIndex(int readerIndex) {
        this.buffer.readerIndex(readerIndex);
        return this;
    }

    @Override
    public int writerIndex() {
        return this.buffer.writerIndex();
    }

    @Override
    public IoBuffer writerIndex(int writerIndex) {
        this.buffer.writerIndex(writerIndex);
        return this;
    }

    @Override
    public int readableBytes() {
        return this.buffer.readableBytes();
    }

    @Override
    public int writableBytes() {
        return this.buffer.writableBytes();
    }

    @Override
    public IoBuffer clear() {
        this.buffer.clear();
        return this;
    }

    @Override
    public IoBuffer markReaderIndex() {
        this.buffer.markReaderIndex();
        return this;
    }

    @Override
    public IoBuffer resetReaderIndex() {
        this.buffer.resetReaderIndex();
        return this;
    }

    @Override
    public IoBuffer markWriterIndex() {
        this.buffer.markWriterIndex();
        return this;
    }

    @Override
    public IoBuffer resetWriterIndex() {
        this.buffer.resetWriterIndex();
        return this;
    }

    @Override
    public boolean getBoolean(int index) {
        return this.buffer.getBoolean(index);
    }

    @Override
    public byte getByte(int index) {
        return this.buffer.getByte(index);
    }

    @Override
    public short getShort(int index) {
        return this.buffer.getShort(index);
    }

    @Override
    public int getMedium(int index) {
        return this.buffer.getMedium(index);
    }

    @Override
    public int getInt(int index) {
        return this.buffer.getInt(index);
    }

    @Override
    public long getLong(int index) {
        return this.buffer.getLong(index);
    }

    @Override
    public char getChar(int index) {
        return this.getChar(index);
    }

    @Override
    public float getFloat(int index) {
        return this.buffer.getFloat(index);
    }

    @Override
    public double getDouble(int index) {
        return this.buffer.getDouble(index);
    }

    @Override
    public IoBuffer getBytes(int index, byte[] dst) {
        this.buffer.getBytes(index, dst);
        return this;
    }

    @Override
    public IoBuffer setBoolean(int index, boolean value) {
        this.buffer.setBoolean(index, value);
        return this;
    }

    @Override
    public IoBuffer setByte(int index, int value) {
        this.buffer.setByte(index, value);
        return this;
    }

    @Override
    public IoBuffer setShort(int index, int value) {
        this.buffer.setShort(index, value);
        return this;
    }

    @Override
    public IoBuffer setMedium(int index, int value) {
        this.buffer.setMedium(index, value);
        return this;
    }

    @Override
    public IoBuffer setInt(int index, int value) {
        this.buffer.setInt(index, value);
        return this;
    }

    @Override
    public IoBuffer setLong(int index, long value) {
        this.buffer.setLong(index, value);
        return this;
    }

    @Override
    public IoBuffer setChar(int index, int value) {
        this.buffer.setChar(index, value);
        return this;
    }

    @Override
    public IoBuffer setFloat(int index, float value) {
        this.buffer.setFloat(index, value);
        return this;
    }

    @Override
    public IoBuffer setDouble(int index, double value) {
        this.buffer.setDouble(index, value);
        return this;
    }

    @Override
    public IoBuffer setBytes(int index, byte[] src) {
        this.buffer.setBytes(index, src);
        return this;
    }

    @Override
    public IoBuffer setBytes(int index, byte[] src, int srcIndex, int length) {
        this.buffer.setBytes(index, src, srcIndex, length);
        return this;
    }

    @Override
    public boolean readBoolean() {
        return this.buffer.readBoolean();
    }

    @Override
    public byte readByte() {
        return this.buffer.readByte();
    }

    @Override
    public short readShort() {
        return this.buffer.readShort();
    }

    @Override
    public int readMedium() {
        return this.buffer.readMedium();
    }

    @Override
    public int readInt() {
        return this.buffer.readInt();
    }

    @Override
    public long readLong() {
        return this.buffer.readLong();
    }

    @Override
    public char readChar() {
        return this.buffer.readChar();
    }

    @Override
    public float readFloat() {
        return this.buffer.readFloat();
    }

    @Override
    public double readDouble() {
        return this.buffer.readDouble();
    }

    @Override
    public IoBuffer readSlice(int length) {
        this.buffer.readSlice(length);
        return this;
    }

    @Override
    public IoBuffer retain() {
        this.buffer.retain();
        return this;
    }

    @Override
    public IoBuffer touch() {
        this.buffer.touch();
        return this;
    }

    @Override
    public IoBuffer skipBytes(int length) {
        this.buffer.skipBytes(length);
        return this;
    }

    @Override
    public IoBuffer writeBoolean(boolean value) {
        this.buffer.writeBoolean(value);
        return this;
    }

    @Override
    public IoBuffer writeByte(int value) {
        this.buffer.writeByte(value);
        return this;
    }

    @Override
    public IoBuffer writeShort(int value) {
        this.buffer.writeShort(value);
        return this;
    }

    @Override
    public IoBuffer writeMedium(int value) {
        this.buffer.writeMedium(value);
        return this;
    }

    @Override
    public IoBuffer writeInt(int value) {
        this.buffer.writeInt(value);
        return this;
    }

    @Override
    public IoBuffer writeLong(long value) {
        this.buffer.writeLong(value);
        return this;
    }

    @Override
    public IoBuffer writeChar(int value) {
        this.buffer.writeChar(value);
        return this;
    }

    @Override
    public IoBuffer writeFloat(float value) {
        this.buffer.writeFloat(value);
        return this;
    }

    @Override
    public IoBuffer writeDouble(double value) {
        this.buffer.writeDouble(value);
        return this;
    }

    @Override
    public IoBuffer writeBytes(byte[] src) {
        this.buffer.writeBytes(src);
        return this;
    }

    @Override
    public IoBuffer writeBytes(byte[] src, int srcIndex, int length) {
        this.buffer.writeBytes(src, srcIndex, length);
        return this;
    }

    @Override
    public IoBuffer slice() {
        return new Buffer(this.buffer.slice());
    }

    @Override
    public IoBuffer slice(int index, int length) {
        return new Buffer(this.buffer.slice(index, length));
    }

    @Override
    public IoBuffer copy() {
        return new Buffer(this.buffer.copy());
    }

    @Override
    public boolean hasArray() {
        return this.buffer.hasArray();
    }

    @Override
    public byte[] array() {
        return this.buffer.array();
    }

    @Override
    public int arrayOffset() {
        return this.buffer.arrayOffset();
    }

    @Override
    public Object unwrap() {
        return this.buffer;
    }
}
