package com.fast.fastrpc;

/**
 * @author yiji
 * @version : RpcException.java, v 0.1 2020-07-29
 */
public class RpcException extends RuntimeException {

    public static final int UNKNOWN_EXCEPTION = 0;
    public static final int NETWORK_EXCEPTION = 1;
    public static final int TIMEOUT_EXCEPTION = 2;
    public static final int BIZ_EXCEPTION = 3;
    public static final int SERIALIZATION_EXCEPTION = 4;

    private int code;

    public RpcException() {
        super();
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    public RpcException(int code) {
        super();
        this.code = code;
    }

    public RpcException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public RpcException(int code, String message) {
        super(message);
        this.code = code;
    }

    public RpcException(int code, Throwable cause) {
        super(cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isBizError() {
        return code == BIZ_EXCEPTION;
    }

    public boolean isTimeoutError() {
        return code == TIMEOUT_EXCEPTION;
    }

    public boolean isNetworkError() {
        return code == NETWORK_EXCEPTION;
    }

    public boolean isSerializationError() {
        return code == SERIALIZATION_EXCEPTION;
    }
}