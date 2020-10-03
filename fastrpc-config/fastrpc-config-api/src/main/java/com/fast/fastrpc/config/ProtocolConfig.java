package com.fast.fastrpc.config;

import java.util.Map;

/**
 * @author yiji
 * @version : ProtocolConfig.java, v 0.1 2020-10-01
 */
public class ProtocolConfig extends AbstractConfig {

    // protocol name
    private String name;

    // sub protocol code name
    private String code;

    // service IP address (when there are multiple network cards available)
    private String host;

    // service port
    private Integer port;

    // service binding network interface
    private String device;

    // serialization
    private String serialize;

    // thread pool
    private String pool;

    // netty worker threads
    private Integer workers;

    // biz core threads
    private Integer coreThreads;

    // biz max threads
    private Integer maxThreads;

    // biz thread pool's queue length
    private Integer queues;

    // transporter
    private String transporter;

    // parameters
    private Map<String, String> parameters;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getSerialize() {
        return serialize;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }

    public String getPool() {
        return pool;
    }

    public void setPool(String pool) {
        this.pool = pool;
    }

    public Integer getWorkers() {
        return workers;
    }

    public void setWorkers(Integer workers) {
        this.workers = workers;
    }

    public Integer getCoreThreads() {
        return coreThreads;
    }

    public void setCoreThreads(Integer coreThreads) {
        this.coreThreads = coreThreads;
    }

    public Integer getMaxThreads() {
        return maxThreads;
    }

    public void setMaxThreads(Integer maxThreads) {
        this.maxThreads = maxThreads;
    }

    public Integer getQueues() {
        return queues;
    }

    public void setQueues(Integer queues) {
        this.queues = queues;
    }

    public String getTransporter() {
        return transporter;
    }

    public void setTransporter(String transporter) {
        this.transporter = transporter;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
