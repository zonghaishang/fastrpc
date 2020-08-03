package com.fast.fastrpc.exchange;

import java.util.concurrent.atomic.AtomicLong;

/**
 * @author yiji
 * @version : Request.java, v 0.1 2020-08-03
 */
public class Request {

    private static final AtomicLong INVOKE_ID = new AtomicLong(1);

    private final long id;

    private String version;

    private boolean oneWay = false;

    private boolean heartbeat = false;

    private boolean broken = false;

    private boolean readOnly = false;

    private Object payload;

    public Request() {
        id = newId();
    }

    public Request(long id) {
        this.id = id;
    }

    private static long newId() {
        return INVOKE_ID.getAndIncrement();
    }

    public long getId() {
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

    public void setBroken(boolean mBroken) {
        this.broken = mBroken;
    }

    public Object getData() {
        return payload;
    }

    public void setData(Object msg) {
        payload = msg;
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
}
