package com.fast.fastrpc.config;

import com.fast.fastrpc.common.Constants;
import com.fast.fastrpc.common.utils.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * @author yiji
 * @version : AbstractServiceConfig.java, v 0.1 2020-10-01
 */
public abstract class AbstractServiceConfig extends AbstractInterfaceConfig {

    // whether to export the service
    protected Boolean export;

    // weight
    protected Integer weight;

    // serialization
    protected String serialize;

    protected Boolean register;

    protected List<ProtocolConfig> protocols;

    protected void checkProtocol() {
        if (protocols == null || protocols.isEmpty()) {
            setProtocols(Arrays.asList(new ProtocolConfig()));
        }
        for (ProtocolConfig protocolConfig : protocols) {
            if (StringUtils.isEmpty(protocolConfig.getName())) {
                protocolConfig.setName(Constants.DEFAULT_PROTOCOL);
            }
            injectProperties(protocolConfig);
        }
    }

    public Boolean getExport() {
        return export;
    }

    public void setExport(Boolean export) {
        this.export = export;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public String getSerialize() {
        return serialize;
    }

    public void setSerialize(String serialize) {
        this.serialize = serialize;
    }

    public List<ProtocolConfig> getProtocols() {
        return protocols;
    }

    public void setProtocols(List<ProtocolConfig> protocols) {
        this.protocols = protocols;
    }

    public Boolean getRegister() {
        return register;
    }

    public void setRegister(Boolean register) {
        this.register = register;
    }
}
