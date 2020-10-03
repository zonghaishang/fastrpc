package com.fast.fastrpc.config;

import java.util.Map;

/**
 * @author yiji
 * @version : AbstractMethodConfig.java, v 0.1 2020-10-01
 */
public abstract class AbstractMethodConfig extends AbstractConfig {

    // request timeout milliseconds
    protected Integer timeout;

    // request retry times if failed.
    protected Integer retries;

    // request invoke type, sync、async、oneway
    protected String invoke;

    // customized parameters
    protected Map<String, String> parameters;

    public Integer getTimeout() {
        return timeout;
    }

    public void setTimeout(Integer timeout) {
        this.timeout = timeout;
    }

    public Integer getRetries() {
        return retries;
    }

    public void setRetries(Integer retries) {
        this.retries = retries;
    }

    public String getInvoke() {
        return invoke;
    }

    public void setInvoke(String invoke) {
        this.invoke = invoke;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }
}
