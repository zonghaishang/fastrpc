package com.fast.fastrpc.common.logger.log4j;

import com.fast.fastrpc.common.logger.Logger;
import com.fast.fastrpc.common.logger.LoggerAdapter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.config.Configurator;

/**
 * @author yiji
 * @version : Log4jLoggerAdapter.java, v 0.1 2020-07-13
 */
public class Log4jLoggerAdapter implements LoggerAdapter {

    private Logger.Level level;

    @Override
    public Logger getLogger(Class<?> key) {
        return getLogger(key.getName());
    }

    @Override
    public Logger getLogger(String key) {
        return new Log4jLogger(LogManager.getLogger(key));
    }

    @Override
    public Logger.Level getLevel() {
        Level logLevel = LogManager.getRootLogger().getLevel();

        if (logLevel.intLevel() <= Level.OFF.intLevel()) return Logger.Level.OFF;
        if (logLevel.intLevel() <= Level.ERROR.intLevel()) return Logger.Level.ERROR;
        if (logLevel.intLevel() <= Level.WARN.intLevel()) return Logger.Level.WARN;
        if (logLevel.intLevel() <= Level.INFO.intLevel()) return Logger.Level.INFO;
        if (logLevel.intLevel() <= Level.DEBUG.intLevel()) return Logger.Level.DEBUG;

        return Logger.Level.OFF;
    }

    @Override
    public void setLevel(Logger.Level level) {

        if (level == this.level) {
            return;
        }

        Level logLevel = Level.INFO;

        if (level == Logger.Level.OFF) logLevel = Level.OFF;
        else if (level == Logger.Level.ERROR) logLevel = Level.ERROR;
        else if (level == Logger.Level.WARN) logLevel = Level.WARN;
        else if (level == Logger.Level.DEBUG) logLevel = Level.DEBUG;

        Configurator.setRootLevel(logLevel);
    }
}