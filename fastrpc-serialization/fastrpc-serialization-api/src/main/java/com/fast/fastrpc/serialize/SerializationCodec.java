package com.fast.fastrpc.serialize;

import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.common.spi.ExtensionLoader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yiji
 * @version : SerializationCodec.java, v 0.1 2020-08-18
 */
public class SerializationCodec {

    private static Map<Integer, Serialization> idSerializations = new HashMap<>();

    private final static ExtensionLoader<Serialization> serializationExtensionLoader = ExtensionLoader.getExtensionLoader(Serialization.class);

    protected static Logger logger = LoggerFactory.getLogger(SerializationCodec.class);

    protected static String supportSerialization;

    static {
        loadSerialization();
    }

    private static void loadSerialization() {
        Set<String> supported = serializationExtensionLoader.getSupportedExtensions();
        StringBuffer buffer = new StringBuffer();
        if (supported != null && !supported.isEmpty()) {
            for (String name : supported) {
                Serialization serialization = serializationExtensionLoader.getExtension(name);
                if (idSerializations.containsKey(serialization.getContentId())) {
                    logger.error("Found duplicate serialization codec " + name
                            + " type " + serialization.getClass().getName()
                            + " id " + serialization.getContentId()
                            + ", existed codec " + idSerializations.get(serialization.getContentId()).getClass().getName());
                    continue;
                }
                idSerializations.put(serialization.getContentId(), serialization);

                if (buffer.length() > 0) {
                    buffer.append(",");
                }
                buffer.append(serialization.getContentId()).append("->").append(serialization.getName());
            }
        }
        supportSerialization = buffer.toString();
    }

    public static Serialization getSerialization(int id) throws IOException {
        Serialization serialization = idSerializations.get(id);
        if (serialization == null) {
            throw new IOException("unsupported serialization id " + id + ", available : " + supportSerialization);
        }

        return serialization;
    }
}
