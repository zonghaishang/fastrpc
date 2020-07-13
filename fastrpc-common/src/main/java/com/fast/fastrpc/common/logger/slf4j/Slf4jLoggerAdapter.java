package com.fast.fastrpc.common.logger.slf4j;

import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerAdapter;

/**
 * @author yiji
 * @version : Slf4jLoggerAdapter.java, v 0.1 2020-07-12
 */
public class Slf4jLoggerAdapter implements LoggerAdapter {

    private Logger.Level level;

    @Override
    public Logger getLogger(Class<?> key) {
        return getLogger(key.getName());
    }

    @Override
    public Logger getLogger(String key) {
        return new Slf4jLogger(org.slf4j.LoggerFactory.getLogger(key));
    }

    @Override
    public Logger.Level getLevel() {
        return level;
    }

    @Override
    public void setLevel(Logger.Level level) {
        this.level = level;
        // todo The logging level should be triggered here, but no slf4j API has been found
    }
}