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

    public static final Pattern COMMA_SPLIT_PATTERN = Pattern.compile("\\s*[,]+\\s*");

    public static final String DEFAULT_PROPERTIES_KEY = "default.properties.file";

    public static final String DEFAULT_PROPERTIES = "fast-rpc.properties";

    public static final String DEFAULT_GLOBAL_PROPERTIES = "default-fast-rpc.properties";

}