package com.fast.fastrpc.common.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author yiji
 * @version : NetUtils.java, v 0.1 2020-07-14
 */
public class NetUtils {

    public static String getIpByHost(String hostName) {
        try {
            return InetAddress.getByName(hostName).getHostAddress();
        } catch (UnknownHostException e) {
            return hostName;
        }
    }

}
