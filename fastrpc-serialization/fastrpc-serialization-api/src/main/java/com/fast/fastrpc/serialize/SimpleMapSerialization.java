package com.fast.fastrpc.serialize;

import com.fast.fastrpc.remoting.netty.buffer.IoBuffer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @author yiji
 * @version : SimpleMapSerialization.java, v 0.1 2020-08-19
 */
public class SimpleMapSerialization {

    public Map<String, String> decodeAttachment(IoBuffer buffer, int length) throws IOException {
        Map<String, String> attachment = new HashMap<>();
        try {
            while (length > 0) {
                length -= 8;
                attachment.put(readString(buffer), readString(buffer));
            }
        } catch (Throwable e) {
            throw new IOException("Failed to decode attachment.", e);
        }
        return attachment;
    }

    public void encodeAttachment(IoBuffer buffer, Map<String, String> attachment) throws IOException {
        try {
            for (Iterator<Map.Entry<String, String>> iterator = attachment.entrySet().iterator(); iterator.hasNext(); ) {
                Map.Entry<String, String> item = iterator.next();
                if (item.getKey() == null || item.getValue() == null) {
                    continue;
                }
                buffer.writeInt(item.getKey().length());
                buffer.writeCharSequence(item.getKey());

                buffer.writeInt(item.getValue().length());
                buffer.writeCharSequence(item.getValue());
            }
        } catch (Throwable e) {
            throw new IOException("Failed to encode attachment.", e);
        }
    }


    private String readString(IoBuffer buffer) {
        int len = buffer.readInt();
        if (len > 0) {
            return buffer.readCharSequence(len).toString();
        }
        return "";
    }

}
