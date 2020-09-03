package com.fast.fastrpc.common.utils;

/**
 * @author yiji
 * @version : StringUtils.java, v 0.1 2020-09-03
 */
public class StringUtils {

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

}
