package com.fast.fastrpc.config.spring.schema;

import com.fast.fastrpc.common.utils.StringUtils;
import com.fast.fastrpc.config.spring.ReferenceBean;
import com.fast.fastrpc.config.spring.ServiceBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.w3c.dom.Element;

/**
 * @author yiji
 * @version : RpcBeanDefinitionParser.java, v 0.1 2020-10-20
 */
public class RpcBeanDefinitionParser implements BeanDefinitionParser {

    private final Class<?> beanClass;

    public RpcBeanDefinitionParser(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public BeanDefinition parse(Element element, ParserContext context) {

        RootBeanDefinition beanDefinition = new RootBeanDefinition();
        beanDefinition.setBeanClass(beanClass);
        beanDefinition.setLazyInit(false);
        String id = element.getAttribute("id");
        if (StringUtils.isEmpty(id)) {
            id = generateId(element, context);
        }

        checkDuplicateBean(context, id);
        context.getRegistry().registerBeanDefinition(id, beanDefinition);
        beanDefinition.getPropertyValues().addPropertyValue("id", id);

        parseProtocol(element, context, beanDefinition);

        return beanDefinition;
    }

    protected void parseProtocol(Element element, ParserContext context, RootBeanDefinition beanDefinition) {

    }

    protected void parseRegistry(Element element, ParserContext context, RootBeanDefinition beanDefinition) {

    }

    protected void parseApplication(Element element, ParserContext context, RootBeanDefinition beanDefinition) {

    }

    protected void parseService(Element element, ParserContext context, RootBeanDefinition beanDefinition) {

    }

    protected void parseReference(Element element, ParserContext context, RootBeanDefinition beanDefinition) {

    }

    protected void checkDuplicateBean(ParserContext parserContext, String id) {
        if (parserContext.getRegistry().containsBeanDefinition(id)) {
            throw new IllegalStateException("Duplicate spring bean id " + id);
        }
    }

    protected String generateId(Element element, ParserContext parserContext) {
        String name = element.getAttribute("name");
        if (StringUtils.isEmpty(name)) {
            name = this.beanClass.getName();
            if (ReferenceBean.class.equals(beanClass)) {
                name = getInterfaceName(element, false) + "@reference";
            } else if (ServiceBean.class.equals(beanClass)) {
                name = getInterfaceName(element, true) + "@service";
            }
        }

        int counter = 0;
        String id = name;
        while (parserContext.getRegistry().containsBeanDefinition(id)) {
            id = name + (counter++);
        }

        return id;
    }

    protected String getInterfaceName(Element element, boolean service) {
        String name = element.getAttribute("interface");
        if (StringUtils.isEmpty(name)) {
            throw new IllegalStateException((service ? "service" : "reference") + " interface is required.");
        }
        return name;
    }

}
