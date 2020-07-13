package com.fast.fastrpc.common.logger;

/**
 * @author yiji
 * @version : LoggerAdapter.java, v 0.1 2020-07-12
 */
public interface LoggerAdapter {

    /**
     * Get a logger
     *
     * @param key get logger by class name
     * @return logger
     */
    Logger getLogger(Class<?> key);

    /**
     * Get a logger
     *
     * @param key get logger by package
     * @return logger
     */
    Logger getLogger(String key);

    /**
     * Get the current logging level
     *
     * @return current logging level
     */
    Logger.Level getLevel();

    /**
     * Set the current logging level
     *
     * @param level logging level
     */
    void setLevel(Logger.Level level);

}