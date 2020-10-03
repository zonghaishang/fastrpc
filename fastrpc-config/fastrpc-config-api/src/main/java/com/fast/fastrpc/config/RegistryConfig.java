package com.fast.fastrpc.config;

import java.util.Map;

/**
 * @author yiji
 * @version : RegistryConfig.java, v 0.1 2020-10-01
 */
public class RegistryConfig extends AbstractConfig {

    // register center address
    private String address;

    // username to login register center
    private String username;

    // password to login register center
    private String password;

    // protocol for register center
    private String protocol;

    // registry client impl
    private String transporter;

    // registry root directory
    private String group;

    // request timeout in milliseconds for register center
    private Integer timeout;

    // file for saving register center dynamic list
    private String cacheFile;

    // whether to check if register center is available when boot up
    private Boolean check;

    // whether to export service on the register center
    private Boolean register;

    // whether allow to subscribe service on the register center
    private Boolean subscribe;

    // customized parameters
    private Map<String, String> parameters;

    // if it's default
    private Boolean isDefault;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getTransporter() {
        return transporter;
    }

    public void setTransporter(String transporter) {
        this.transporter = transporter;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public String getCacheFile() {
        return cacheFile;
    }

    public void setCacheFile(String cacheFile) {
        this.cacheFile = cacheFile;
    }

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public Boolean getRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }

    public Boolean getSubscribe() {
        return subscribe;
    }

    public void setSubscribe(Boolean subscribe) {
        this.subscribe = subscribe;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

    public Boolean getDefault() {
        return isDefault;
    }

    public void setDefault(Boolean aDefault) {
        isDefault = aDefault;
    }
}
