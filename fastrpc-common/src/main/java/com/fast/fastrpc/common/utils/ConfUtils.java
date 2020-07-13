package com.fast.fastrpc.common.utils;

import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * @author yiji
 * @version : ConfUtils.java, v 0.1 2020-07-13
 */
public class ConfUtils {

    private static final Logger logger = LoggerFactory.getLogger(ConfUtils.class);

    private static volatile Properties PROPERTIES;

    private ConfUtils() {
    }

    public static String getProperty(String key) {
        return getProperty(key, null);
    }

    public static String getProperty(String key, String defaultValue) {
        String value = System.getProperty(key);
        if (value != null && value.length() > 0) {
            return value;
        }
        return getProperties().getProperty(key, defaultValue);
    }

    public static Properties getProperties() {
        if (PROPERTIES == null) {
            synchronized (ConfUtils.class) {
                if (PROPERTIES == null) {
                    // load default property first
                    Properties properties = loadProperties(Constants.DEFAULT_GLOBAL_PROPERTIES, false, true);
                    String path = System.getProperty(Constants.DEFAULT_PROPERTIES_KEY);
                    if (path == null || (path = path.trim()).length() == 0) {
                        path = System.getenv(Constants.DEFAULT_PROPERTIES_KEY);
                        if (path == null || path.length() == 0) {
                            path = Constants.DEFAULT_PROPERTIES;
                        }
                    }
                    // maybe override default-fast-rpc.properties
                    properties.putAll(loadProperties(path, false, true));
                    PROPERTIES = properties;
                }
            }
        }
        return PROPERTIES;
    }

    public static Properties loadProperties(String fileName, boolean allowMultiFile, boolean optional) {
        Properties properties = new Properties();
        // find absolute file path.
        if (fileName.startsWith("/")) {
            try {
                FileInputStream input = new FileInputStream(fileName);
                try {
                    properties.load(input);
                } finally {
                    input.close();
                }
            } catch (Throwable e) {
                logger.warn("Failed to load " + fileName + " file from " + fileName, e);
            }
            return properties;
        }

        List<URL> list = new ArrayList<URL>();
        try {
            Enumeration<URL> urls = ConfUtils.class.getClassLoader().getResources(fileName);
            list = new ArrayList<java.net.URL>();
            while (urls.hasMoreElements()) {
                list.add(urls.nextElement());
            }
        } catch (Throwable t) {
            logger.warn("Fail to load " + fileName, t);
        }

        if (list.isEmpty()) {
            if (!optional) {
                logger.warn("No " + fileName + " found on the class path.");
            }
            return properties;
        }

        if (!allowMultiFile) {
            if (list.size() > 1) {
                String errMsg = String.format("only 1 %s file is expected, but %d %s files found on class path: %s",
                        fileName, list.size(), fileName, list.toString());
                logger.warn(errMsg);
            }

            try {
                URL url = ConfUtils.class.getClassLoader().getResource(fileName);
                if (url != null) {
                    logger.info("load " + fileName + " file from " + url);
                    InputStream input = url.openStream();
                    try {
                        properties.load(input);
                    } finally {
                        try {
                            input.close();
                        } catch (Throwable t) {
                        }
                    }
                }
            } catch (Throwable e) {
                logger.warn("Failed to load " + fileName + " file from " + fileName, e);
            }
            return properties;
        }

        logger.info("load " + fileName + " file from " + list);

        for (java.net.URL url : list) {
            try {
                Properties p = new Properties();
                InputStream input = url.openStream();
                if (input != null) {
                    try {
                        p.load(input);
                        properties.putAll(p);
                    } finally {
                        try {
                            input.close();
                        } catch (Throwable t) {
                        }
                    }
                }
            } catch (Throwable e) {
                logger.warn("Fail to load " + fileName + " file from " + url, e);
            }
        }

        return properties;
    }

}