package com.fast.fastrpc.config;

import com.fast.fastrpc.config.annotation.Parameter;

import java.util.Map;

/**
 * @author yiji
 * @version : ApplicationConfig.java, v 0.1 2020-10-01
 */
public class ApplicationConfig extends AbstractConfig {

    public static final String NAME = "application";

    // application name
    @Parameter(name = "app")
    private String name;

    // environment, eg: dev, test or prod
    private String environment;

    // logger
    private String logger;

    // customized parameters
    private Map<String, String> parameters;

    // application owner
    private String owner;

    // warm up period
    private Integer warmup;

    private Integer shutdownTimeout;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getLogger() {
        return logger;
    }

    public void setLogger(String logger) {
        this.logger = logger;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getWarmup() {
        return warmup;
    }

    public void setWarmup(Integer warmup) {
        this.warmup = warmup;
    }

    public Integer getShutdownTimeout() {
        return shutdownTimeout;
    }

    public void setShutdownTimeout(Integer shutdownTimeout) {
        this.shutdownTimeout = shutdownTimeout;
    }
}
