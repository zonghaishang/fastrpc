/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.fast.fastrpc.common.logger;

import com.fast.fastrpc.common.logger.log4j.Log4jLoggerAdapter;
import com.fast.fastrpc.common.logger.slf4j.Slf4jLoggerAdapter;

import java.util.concurrent.ConcurrentHashMap;

/**
 * @author yiji
 * @version : LoggerFactory.java, v 0.1 2020-07-13
 */
public class LoggerFactory {

    private static final ConcurrentHashMap<String, Logger> ALL_LOGGERS = new ConcurrentHashMap<>();

    private static volatile LoggerAdapter LOGGER_ADAPTER;

    static {
        String logger = System.getProperty("default.application.logger");
        if ("slf4j".equals(logger)) {
            setLoggerAdapter(new Slf4jLoggerAdapter());
        } else if ("log4j".equals(logger)) {
            setLoggerAdapter(new Log4jLoggerAdapter());
        } else {
            try {
                setLoggerAdapter(new Log4jLoggerAdapter());
            } catch (Throwable e) {
                try {
                    setLoggerAdapter(new Slf4jLoggerAdapter());
                } catch (Throwable ex) {
                }
            }
        }
    }

    private LoggerFactory() {
    }

    public static void setLoggerAdapter(LoggerAdapter loggerAdapter) {
        if (loggerAdapter != null) {
            Logger logger = loggerAdapter.getLogger(LoggerFactory.class.getName());
            logger.info("using logger: " + loggerAdapter.getClass().getName());
            LoggerFactory.LOGGER_ADAPTER = loggerAdapter;
        }
    }

    public static Logger getLogger(Class<?> key) {
        return getLogger(key.getName());
    }

    /**
     * Get logger provider
     *
     * @param key the returned logger will be named after key
     * @return logger provider
     */
    public static Logger getLogger(String key) {
        Logger logger = ALL_LOGGERS.get(key);
        if (logger == null) {
            ALL_LOGGERS.putIfAbsent(key, LOGGER_ADAPTER.getLogger(key));
            logger = ALL_LOGGERS.get(key);
        }
        return logger;
    }

    /**
     * Get logging level
     *
     * @return logging level
     */
    public static Logger.Level getLevel() {
        return LOGGER_ADAPTER.getLevel();
    }

    /**
     * Set the current logging level
     *
     * @param level logging level
     */
    public static void setLevel(Logger.Level level) {
        LOGGER_ADAPTER.setLevel(level);
    }

}