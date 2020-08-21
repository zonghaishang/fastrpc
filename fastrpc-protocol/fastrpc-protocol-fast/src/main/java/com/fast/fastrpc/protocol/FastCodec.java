package com.fast.fastrpc.protocol;

import com.fast.fastrpc.Codec;
import com.fast.fastrpc.ProtocolCodec;
import com.fast.fastrpc.channel.Channel;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.common.spi.ExtensionLoader;
import com.fast.fastrpc.common.buffer.IoBuffer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import static com.fast.fastrpc.protocol.codec.AbstractFastCodec.magicIndex;
import static com.fast.fastrpc.protocol.codec.AbstractFastCodec.versionIndex;

/**
 * @author yiji
 * @version : FastCodec.java, v 0.1 2020-08-17
 */
public class FastCodec implements Codec {

    private static final Logger logger = LoggerFactory.getLogger(FastCodec.class);

    public static final byte MAGIC = (byte) 0xAF;

    private static final Map<Integer, ProtocolCodec> protocolCodecs = new HashMap<>();

    private final static ExtensionLoader<Codec> codecExtensionLoader = ExtensionLoader.getExtensionLoader(Codec.class);

    static {
        loadFastCodec();
    }

    @Override
    public void encode(Channel channel, IoBuffer buffer, Object message) throws IOException {
        int version = channel.getUrl().getParameter(Constants.PROTOCOL_VERSION, Constants.DEFAULT_PROTOCOL_VERSION);
        ProtocolCodec codec = getProtocolCodec(version);
        codec.encode(channel, buffer, message);
    }

    @Override
    public Object decode(Channel channel, IoBuffer buffer) throws IOException {
        int readable = buffer.readableBytes();
        if (readable < 2) return null;

        byte magic = buffer.getByte(magicIndex);
        if (magic != MAGIC) {
            throw new IOException("unsupported magic '" + magic + "', expect '0xAF'.");
        }

        int version = buffer.getByte(versionIndex);
        ProtocolCodec codec = getProtocolCodec(version);

        return codec.decode(channel, buffer);
    }

    private ProtocolCodec getProtocolCodec(int version) throws IOException {
        ProtocolCodec codec = protocolCodecs.get(version);
        if (codec == null) {
            throw new IOException("unsupported fast protocol version " + version);
        }
        return codec;
    }

    private static void loadFastCodec() {
        Set<String> supported = codecExtensionLoader.getSupportedExtensions();
        if (supported != null && !supported.isEmpty()) {
            for (String name : supported) {
                Codec codec = codecExtensionLoader.getExtension(name);
                if (codec instanceof ProtocolCodec) {
                    ProtocolCodec loadedCodec = (ProtocolCodec) codec;
                    if (protocolCodecs.containsKey(loadedCodec.getVersion())) {
                        logger.error("Found duplicate protocol codec " + name
                                + " type " + codec.getClass().getName()
                                + " version " + loadedCodec.getVersion()
                                + ", existed codec " + protocolCodecs.get(loadedCodec.getVersion()).getClass().getName());
                        continue;
                    }
                    protocolCodecs.put(loadedCodec.getVersion(), loadedCodec);
                }
            }
        }
    }
}
