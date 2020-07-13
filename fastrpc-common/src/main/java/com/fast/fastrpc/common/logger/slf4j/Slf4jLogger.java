/**
 * Alipay.com Inc.
 * Copyright (c) 2004-2020 All Rights Reserved.
 */
package com.fast.fastrpc.common.logger.slf4j;

import com.fast.fastrpc.common.logger.Logger;

/**
 * @author yiji
 * @version : Slf4jLogger.java, v 0.1 2020-07-12
 */
class Slf4jLogger implements Logger {

    private final org.slf4j.Logger logger;

    Slf4jLogger(org.slf4j.Logger logger) {
        this.logger = logger;
    }

    @Override
    public void debug(String msg) {
        this.logger.debug(msg);
    }

    @Override
    public void debug(String msg, Throwable e) {
        this.logger.debug(msg, e);
    }

    @Override
    public void info(String msg) {
        this.logger.info(msg);
    }

    @Override
    public void info(String msg, Throwable e) {
        this.logger.info(msg, e);
    }

    @Override
    public void warn(String msg) {
        this.logger.warn(msg);
    }

    @Override
    public void warn(String msg, Throwable e) {
        this.logger.warn(msg, e);
    }

    @Override
    public void error(String msg) {
        this.logger.error(msg);
    }

    @Override
    public void error(String msg, Throwable e) {
        this.logger.error(msg, e);
    }

    @Override
    public boolean isDebugEnabled() {
        return this.logger.isDebugEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return this.logger.isInfoEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return this.logger.isWarnEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return this.logger.isErrorEnabled();
    }
}