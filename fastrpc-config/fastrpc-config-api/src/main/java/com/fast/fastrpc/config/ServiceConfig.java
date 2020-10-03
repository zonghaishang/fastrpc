package com.fast.fastrpc.config;

import com.fast.fastrpc.Exporter;
import com.fast.fastrpc.Invoker;
import com.fast.fastrpc.Protocol;
import com.fast.fastrpc.ProxyFactory;
import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.URL;
import com.fast.fastrpc.common.spi.ExtensionLoader;
import com.fast.fastrpc.common.utils.NetUtils;
import com.fast.fastrpc.common.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author yiji
 * @version : ServiceConfig.java, v 0.1 2020-10-01
 */
public class ServiceConfig<T> extends AbstractServiceConfig {

    protected T ref;

    protected String path;

    protected AtomicBoolean exported = new AtomicBoolean();

    protected AtomicBoolean destroyed = new AtomicBoolean();

    private final List<Exporter<?>> exporters = new ArrayList<>();

    private static final Protocol protocol = ExtensionLoader.getExtensionLoader(Protocol.class).getAdaptiveExtension();

    private static final ProxyFactory proxyFactory = ExtensionLoader.getExtensionLoader(ProxyFactory.class).getAdaptiveExtension();

    public synchronized void export() {
        if (export != null && !export) {
            return;
        }
        doExport();
    }

    protected synchronized void doExport() {
        if (destroyed.get()) {
            throw new IllegalStateException("Already exported!");
        }

        if (exported.compareAndSet(false, true)) {
            checkInterfaceAndMethods();
            checkRefInstance();
            checkApplication();
            checkRegistry();
            checkProtocol();
            injectProperties(this);
            doExportUrls();
        }
    }

    protected void checkRefInstance() {
        // reference should not be null, and is the implementation of the given interface
        if (ref == null) {
            throw new IllegalStateException("ref not allow null!");
        }
        if (!interfaceClass.isInstance(ref)) {
            throw new IllegalStateException("The class "
                    + ref.getClass().getName() + " unimplemented interface "
                    + interfaceClass + "!");
        }
    }

    protected void doExportUrls() {
        List<URL> registryURLs = loadRegistries(true);
        for (ProtocolConfig protocolConfig : protocols) {
            doExportUrlsForProtocol(protocolConfig, registryURLs);
        }
    }

    protected void doExportUrlsForProtocol(ProtocolConfig protocolConfig, List<URL> registryURLs) {
        String protocolName = protocolConfig.getName();
        if (protocolName == null || protocolName.length() == 0) {
            protocolName = "fast";
        }

        Map<String, String> map = new HashMap<>();
        map.put(Constants.SIDE_KEY, Constants.SERVER_KEY);
        appendParameters(map, application);
        appendParameters(map, protocolConfig);
        appendParameters(map, this);
        if (methods != null && !methods.isEmpty()) {
            for (MethodConfig method : methods) {
                appendParameters(map, method, method.getName());
            }
        }

        // export service
        String host = this.findHost(protocolConfig);
        Integer port = this.findPort(protocolConfig, protocolName, map);

        map.remove(Constants.CODE_KEY);
        URL url = new URL(protocolName, host, port, path, map);

        // export jvm service
        if (logger.isInfoEnabled()) {
            logger.info("export jvm service " + interfaceClass.getName() + " url " + url + "");
        }
        exportLocal(url);

        String code = protocolConfig.getCode();
        if (StringUtils.isEmpty(code)) {
            // The fast sub protocol tr(Thor) is used by default.
            code = exportDirect("fast", protocolName) ? "tr" : protocolName;
        }

        if (exportDirect(protocolName, code)) {
            doExportProtocol(registryURLs, code, url);
        } else {
            doExportSubProtocol(registryURLs, url, code);
        }
    }

    protected void doExportProtocol(List<URL> registryURLs, String codeName, URL url) {
        if (registryURLs != null && !registryURLs.isEmpty()) {
            for (URL registryURL : registryURLs) {
                url = url.addParameterIfAbsent(Constants.DYNAMIC_KEY, registryURL.getParameter(Constants.DYNAMIC_KEY));
                if (logger.isInfoEnabled()) {
                    logger.info("Register " + codeName + " service " + interfaceClass.getName() + " url " + url + " to registry " + registryURL);
                }
                String proxy = url.getParameter(Constants.PROXY_KEY);
                if (StringUtils.isNotEmpty(proxy)) {
                    registryURL = registryURL.addParameter(Constants.PROXY_KEY, proxy);
                }
                Invoker<?> invoker = proxyFactory.getInvoker(ref, (Class) interfaceClass, registryURL.addEncodedParameter(Constants.EXPORT_KEY, url.toFullString()));
                Exporter<?> exporter = protocol.export(invoker);
                exporters.add(exporter);
            }
        } else {
            Invoker<?> invoker = proxyFactory.getInvoker(ref, (Class) interfaceClass, url);
            Exporter<?> exporter = protocol.export(invoker);
            exporters.add(exporter);
        }
    }

    protected void doExportSubProtocol(List<URL> registryURLs, URL url, String code) {
        for (String codeName : Constants.COMMA_SPLIT_PATTERN.split(code)) {
            URL exportURL = url.addParameter(Constants.PROTOCOL_VERSION, wrapCodeName(codeName));
            doExportProtocol(registryURLs, codeName, exportURL);
        }
    }

    protected boolean exportDirect(String protocolName, String code) {
        return !code.contains(Constants.COMMA_SEPARATOR) && code.equals(protocolName);
    }

    protected void exportLocal(URL url) {
        if (!Constants.LOCAL_PROTOCOL.equalsIgnoreCase(url.getProtocol())) {
            URL local = URL.valueOf(url.toFullString())
                    .setProtocol(Constants.LOCAL_PROTOCOL)
                    .setPort(0);
            Exporter<?> exporter = protocol.export(
                    proxyFactory.getInvoker(ref, (Class) interfaceClass, local));
            exporters.add(exporter);
            logger.info("Export jvm service " + interfaceClass.getName() + " to local registry");
        }
    }

    private String findHost(ProtocolConfig protocolConfig) {
        String hostToBind = protocolConfig.getHost();
        if (StringUtils.isNotEmpty(hostToBind) && NetUtils.isInvalidLocalHost(hostToBind)) {
            throw new IllegalArgumentException("Specified invalid bind ip from protocol, value:" + hostToBind);
        }

        if (StringUtils.isEmpty(hostToBind)) {
            hostToBind = NetUtils.getIpByDevice(protocolConfig.getDevice());
            return hostToBind == null || hostToBind.length() == 0
                    ? NetUtils.getLocalHost()
                    : hostToBind;
        }
        return hostToBind;
    }

    private Integer findPort(ProtocolConfig protocolConfig, String name, Map<String, String> map) {
        String port = protocolConfig.getPool();
        Integer portToBind = parsePort(port);

        if (portToBind == null) {
            final int defaultPort = ExtensionLoader.getExtensionLoader(Protocol.class).getExtension(name).getDefaultPort();
            portToBind = defaultPort;
        }

        return portToBind;
    }

    private Integer parsePort(String configPort) {
        Integer port = null;
        if (StringUtils.isNotEmpty(configPort)) {
            try {
                Integer intPort = Integer.parseInt(configPort);
                if (intPort <= 0) {
                    throw new IllegalArgumentException("Specified invalid port from protocol config value :" + configPort);
                }
                port = intPort;
            } catch (Exception e) {
                throw new IllegalArgumentException("Specified invalid port from protocol config value:" + configPort);
            }
        }
        return port;
    }

    protected String wrapCodeName(String codeName) {
        ExtensionLoader<Protocol> protocolExtensionLoader = ExtensionLoader.getExtensionLoader(Protocol.class);
        Set<String> supported = protocolExtensionLoader.getSupportedExtensions();
        if (supported != null && !supported.isEmpty()) {
            for (String name : supported) {
                Protocol protocol = protocolExtensionLoader.getExtension(name);
                if (protocol.getName().equals(codeName)) {
                    return protocol.getVersion();
                }
            }
        }
        return codeName;
    }

    public T getRef() {
        return ref;
    }

    public void setRef(T ref) {
        this.ref = ref;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
