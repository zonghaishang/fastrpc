package com.fast.fastrpc.common.logger;

/**
 * @author yiji
 * @version : Logger.java, v 0.1 2020-07-12
 */
public interface Logger {

    enum Level {

        /**
         * DEBUG
         */
        DEBUG,

        /**
         * INFO
         */
        INFO,

        /**
         * WARN
         */
        WARN,

        /**
         * ERROR
         */
        ERROR,

        /**
         * OFF
         */
        OFF
    }

    /**
     * Logs a message with debug log level.
     *
     * @param msg log this message
     */
    void debug(String msg);

    /**
     * Logs an error with debug log level.
     *
     * @param msg log this message
     * @param e   log this cause
     */
    void debug(String msg, Throwable e);

    /**
     * Logs a message with info log level.
     *
     * @param msg log this message
     */
    void info(String msg);

    /**
     * Logs an error with info log level.
     *
     * @param msg log this message
     * @param e   log this cause
     */
    void info(String msg, Throwable e);

    /**
     * Logs a message with warn log level.
     *
     * @param msg log this message
     */
    void warn(String msg);

    /**
     * Logs a message with warn log level.
     *
     * @param msg log this message
     * @param e   log this cause
     */
    void warn(String msg, Throwable e);

    /**
     * Logs a message with error log level.
     *
     * @param msg log this message
     */
    void error(String msg);

    /**
     * Logs an error with error log level.
     *
     * @param msg log this message
     * @param e   log this cause
     */
    void error(String msg, Throwable e);

    /**
     * Is debug logging currently enabled?
     *
     * @return true if debug is enabled
     */
    boolean isDebugEnabled();

    /**
     * Is info logging currently enabled?
     *
     * @return true if info is enabled
     */
    boolean isInfoEnabled();

    /**
     * Is warn logging currently enabled?
     *
     * @return true if warn is enabled
     */
    boolean isWarnEnabled();

    /**
     * Is error logging currently enabled?
     *
     * @return true if error is enabled
     */
    boolean isErrorEnabled();

}