package com.fast.fastrpc.config;

/**
 * @author yiji
 * @version : AbstractReferenceConfig.java, v 0.1 2020-10-01
 */
public abstract class AbstractReferenceConfig extends AbstractInterfaceConfig {

    // check if service provider exists
    protected Boolean check;

    // whether to find reference's instance from the current JVM
    protected Boolean jvmFirst;

    // allows only one service to be invoked at all times
    protected Boolean sticky;

    // provider application
    protected String provider;

    public Boolean getCheck() {
        return check;
    }

    public void setCheck(Boolean check) {
        this.check = check;
    }

    public Boolean getJvmFirst() {
        return jvmFirst;
    }

    public void setJvmFirst(Boolean jvmFirst) {
        this.jvmFirst = jvmFirst;
    }

    public Boolean getSticky() {
        return sticky;
    }

    public void setSticky(Boolean sticky) {
        this.sticky = sticky;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}
