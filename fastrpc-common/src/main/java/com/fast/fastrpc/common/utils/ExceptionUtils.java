package com.fast.fastrpc.common.utils;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;

/**
 * @author yiji
 * @version : ExceptionUtils.java, v 0.1 2020-09-01
 */
public class ExceptionUtils {

    public static String toString(Throwable e) {
        if (e == null) return "";
        PrintWriter writer = null;
        try {
            ByteArrayOutputStream buf = new ByteArrayOutputStream();
            writer = new PrintWriter(buf);
            e.printStackTrace(writer);
            writer.flush();
            return buf.toString();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

}
