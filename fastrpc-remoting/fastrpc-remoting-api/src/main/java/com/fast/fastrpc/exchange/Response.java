package com.fast.fastrpc.exchange;

/**
 * @author yiji
 * @version : Response.java, v 0.1 2020-08-03
 */
public class Response {

    public static final byte OK = 0;
    public static final byte CLIENT_TIMEOUT = 1;
    public static final byte SERVER_TIMEOUT = 2;
    public static final byte CHANNEL_INACTIVE = 3;
    public static final byte BAD_REQUEST = 4;
    public static final byte BAD_RESPONSE = 5;
    public static final byte SERVICE_NOT_FOUND = 6;
    public static final byte SERVICE_ERROR = 7;
    public static final byte SERVER_ERROR = 8;
    public static final byte CLIENT_ERROR = 9;
    public static final byte SERVER_POOL_EXHAUSTED_ERROR = 10;

    private long id = 0;

    private String version;

    private byte status = OK;

    private String error;

    private Object payload;

    private boolean heartbeat = false;

    private boolean readOnly = false;

    public Response() {
    }

    public Response(long id) {
        this.id = id;
    }

    public Response(long id, String version) {
        this.id = id;
        this.version = version;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public boolean isHeartbeat() {
        return heartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        this.heartbeat = heartbeat;
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }
}
