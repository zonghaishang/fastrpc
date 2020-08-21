package com.fast.fastrpc.exchange;

import com.fast.fastrpc.EncodeSupport;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author yiji
 * @version : Request.java, v 0.1 2020-08-03
 */
public class Request extends EncodeSupport {

    private static final AtomicInteger INVOKE_ID = new AtomicInteger(1);

    private final int id;

    private String version;

    private boolean oneWay = false;

    private boolean heartbeat = false;

    private boolean broken = false;

    private boolean readOnly = false;

    private int timeout;

    private int serializeId;

    private byte compress;

    private byte protocolVersion;

    private Object payload;

    public Request() {
        id = newId();
    }

    public Request(int id) {
        this.id = id;
    }

    private static int newId() {
        return INVOKE_ID.getAndIncrement();
    }

    public int getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public boolean isOneWay() {
        return oneWay;
    }

    public void setOneWay(boolean oneWay) {
        this.oneWay = oneWay;
    }

    public boolean isBroken() {
        return broken;
    }

    public void setBroken(boolean broken) {
        this.broken = broken;
    }

    public boolean isHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getSerializeId() {
        return serializeId;
    }

    public void setSerializeId(int serializeId) {
        this.serializeId = serializeId;
    }

    public byte getCompress() {
        return compress;
    }

    public void setCompress(byte compress) {
        this.compress = compress;
    }

    public byte getProtocolVersion() {
        return protocolVersion;
    }

    public void setProtocolVersion(byte code) {
        this.protocolVersion = code;
    }
}
