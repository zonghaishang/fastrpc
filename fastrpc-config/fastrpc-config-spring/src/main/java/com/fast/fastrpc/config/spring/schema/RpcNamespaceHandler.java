package com.fast.fastrpc.config.spring.schema;

import com.fast.fastrpc.config.ApplicationConfig;
import com.fast.fastrpc.config.ProtocolConfig;
import com.fast.fastrpc.config.RegistryConfig;
import com.fast.fastrpc.config.spring.ReferenceBean;
import com.fast.fastrpc.config.spring.ServiceBean;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * @author yiji
 * @version : RpcNamespaceHandler.java, v 0.1 2020-10-03
 */
public class RpcNamespaceHandler extends NamespaceHandlerSupport {

    @Override
    public void init() {
        registerBeanDefinitionParser(ApplicationConfig.NAME, new RpcBeanDefinitionParser(ApplicationConfig.class));
        registerBeanDefinitionParser(RegistryConfig.NAME, new RpcBeanDefinitionParser(RegistryConfig.class));
        registerBeanDefinitionParser(ProtocolConfig.NAME, new RpcBeanDefinitionParser(ProtocolConfig.class));
        registerBeanDefinitionParser(ServiceBean.NAME, new RpcBeanDefinitionParser(ServiceBean.class));
        registerBeanDefinitionParser(ReferenceBean.NAME, new RpcBeanDefinitionParser(ReferenceBean.class));
    }

}
