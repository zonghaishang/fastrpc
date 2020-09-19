package com.fast.fastrpc.common;

import java.util.regex.Pattern;

/**
 * @author yiji
 * @version : Constants.java, v 0.1 2020-07-13
 */
public class Constants {

    public static final String BACKUP_KEY = "backup";

    public static final String REMOVE_VALUE_PREFIX = "-";

    public static final String DEFAULT_KEY = "default";

    public static final String UNIQUE_ID = "uniqueId";

    public static final String SERVICE_KEY = "service";

    public static final String CATEGORY_KEY = "category";

    public static final String PROVIDERS = "providers";

    public static final String TIMEOUT_KEY = "timeout";

    public static final String SHUTDOWN_KEY = "shutdown";

    public static final String APPLICATION_KEY = "app";

    public static final String RECONNECT_KEY = "reconnect";

    public static final String CHECK_KEY = "check";

    public static final String CONNECT_TIMEOUT_KEY = "connect";

    public static final String WORKER_THREADS_KEY = "workers";

    public static final String CODEC_IN_IO_KEY = "codecIO";

    public static final String METHOD_KEY = "method";

    public static final String DYNAMIC_KEY = "dynamic";

    public static final String PROTOCOL_VERSION = "p";

    public static final String CODEC_KEY = "codec";

    public static final Integer DEFAULT_PROTOCOL_VERSION = 1;

    public static final String SERIALIZATION_KEY = "serialization";

    public static final String DEFAULT_SERIALIZATION = "hessian4";

    public static final String TRANSPORTER_KEY = "transporter";

    public static final String EXCHANGER_KEY = "exchanger";

    public static final String CONNECTIONS_KEY = "connections";

    public static final String NETTY = "netty";

    public static final String HEADER = "header";

    public static final String SERVER_KEY = "server";

    public static final String CLIENT_KEY = "client";

    public static final String REGISTRY_RETRY_PERIOD_KEY = "retryPeriod";

    public static final String CACHE_PATH = "cacheFile";

    public static final String DISABLE_KEY = "disabled";

    public static final int DEFAULT_WORKER_THREADS = Math.min(Runtime.getRuntime().availableProcessors() + 1, 32);

    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

    public static final String DEFAULT_PROPERTIES_KEY = "default.properties.file";

    public static final String DEFAULT_PROPERTIES = "fast-rpc.properties";

    public static final String DEFAULT_GLOBAL_PROPERTIES = "default-fast-rpc.properties";

}