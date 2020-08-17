package com.fast.fastrpc;

/**
 * @author yiji
 * @version : ProtocolCodec.java, v 0.1 2020-08-17
 */
public interface ProtocolCodec extends Codec {

    /**
     * Supports multiple versions of the same protocol extension.
     */
    int getVersion();

}
