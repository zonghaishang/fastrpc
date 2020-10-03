package com.fast.fastrpc.protocol;

import com.fast.fastrpc.Exporter;
import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.Protocol;
import com.fast.fastrpc.RpcException;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;
import com.fast.fastrpc.common.spi.ExtensionLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author yiji
 * @version : RpcProtocol.java, v 0.1 2020-08-17
 */
public class FastProtocol implements Protocol {

    private static final int DEFAULT_PORT = 20660;

    private static final Logger logger = LoggerFactory.getLogger(FastProtocol.class);

    private static final Map<String, Protocol> protocols = new HashMap<>();

    private final static ExtensionLoader<Protocol> protocolExtensionLoader = ExtensionLoader.getExtensionLoader(Protocol.class);

    static {
        loadFastProtocol();
    }

    @Override
    public <T> Exporter<T> export(Invoker<T> invoker) throws RpcException {
        String version = invoker.getUrl().getParameter(Constants.PROTOCOL_VERSION, String.valueOf(Constants.DEFAULT_PROTOCOL_VERSION));
        Protocol protocol = protocols.get(version);
        if (protocol == null) {
            throw new RpcException("unsupported fast protocol with version " + version + ", url " + invoker.getUrl());
        }
        return protocol.export(invoker);
    }

    @Override
    public <T> Invoker<T> refer(Class<T> type, URL url) throws RpcException {
        String version = url.getParameter(Constants.PROTOCOL_VERSION, String.valueOf(Constants.DEFAULT_PROTOCOL_VERSION));
        Protocol protocol = protocols.get(version);
        if (protocol == null) {
            throw new RpcException("unsupported fast protocol with version " + version + ", url " + url);
        }
        return protocol.refer(type, url);
    }

    @Override
    public void destroy() {
        for (Protocol protocol : protocols.values()) {
            protocol.destroy();
        }
    }

    private static void loadFastProtocol() {
        Set<String> supported = protocolExtensionLoader.getSupportedExtensions();
        if (supported != null && !supported.isEmpty()) {
            for (String name : supported) {
                Protocol protocol = protocolExtensionLoader.getExtension(name);
                if (protocol instanceof AbstractFastProtocol) {
                    AbstractFastProtocol fastProtocol = (AbstractFastProtocol) protocol;
                    if (protocols.containsKey(fastProtocol.getVersion())) {
                        logger.error("Found duplicate protocol " + name
                                + " type " + fastProtocol.getClass().getName()
                                + " version " + fastProtocol.getVersion()
                                + ", existed protocol " + protocols.get(fastProtocol.getVersion()).getClass().getName());
                        continue;
                    }
                    protocols.put(fastProtocol.getVersion(), fastProtocol);
                }
            }
        }
    }

    @Override
    public String getName() {
        return Constants.DEFAULT_PROTOCOL;
    }

    @Override
    public int getDefaultPort() {
        return DEFAULT_PORT;
    }

    @Override
    public String getVersion() {
        return null;
    }
}