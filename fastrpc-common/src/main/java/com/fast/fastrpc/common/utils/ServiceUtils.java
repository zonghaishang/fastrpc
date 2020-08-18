package com.fast.fastrpc.common.utils;

import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.URL;

/**
 * @author yiji
 * @version : ServiceUtils.java, v 0.1 2020-07-30
 */
public class ServiceUtils {

    public static String serviceKey(String path, String uniqueId, String protocol) {
        StringBuffer buf = new StringBuffer();
        if (path != null && path.length() > 0) {
            buf.append(path);
        }
        if (uniqueId != null && uniqueId.length() > 0) {
            buf.append(":").append(uniqueId);
        }
        if (protocol != null && protocol.length() > 0) {
            buf.append("@").append(protocol);
        }
        return buf.toString();
    }

    public static String findServiceKey(URL url) {
        return serviceKey(url.getPath(), url.getParameter(Constants.UNIQUE_ID), url.getProtocol());
    }

}
