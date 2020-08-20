package com.fast.fastrpc.exchange;

/**
 * @author yiji
 * @version : Response.java, v 0.1 2020-08-03
 */
public class Response {

    public static final short OK = 0;
    public static final short CLIENT_TIMEOUT = 1;
    public static final short SERVER_TIMEOUT = 2;
    public static final short CHANNEL_INACTIVE = 3;
    public static final short BAD_REQUEST = 4;
    public static final short BAD_RESPONSE = 5;
    public static final short SERVICE_NOT_FOUND = 6;
    public static final short SERVICE_ERROR = 7;
    public static final short SERVER_ERROR = 8;
    public static final short CLIENT_ERROR = 9;
    public static final short SERVER_POOL_EXHAUSTED_ERROR = 10;

    private int id = 0;

    private String version;

    private short status = OK;

    private String error;

    private Object payload;

    private boolean heartbeat = false;

    private boolean readOnly = false;

    private int serializeId;

    private byte compress;

    private byte protocolVersion;

    public Response() {
    }

    public Response(int id) {
        this.id = id;
    }

    public Response(int id, String version) {
        this.id = id;
        this.version = version;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public short getStatus() {
        return status;
    }

    public void setStatus(short status) {
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

    public void setProtocolVersion(byte protocolVersion) {
        this.protocolVersion = protocolVersion;
    }
}
