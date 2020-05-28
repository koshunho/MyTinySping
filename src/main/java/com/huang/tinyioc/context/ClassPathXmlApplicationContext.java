package com.huang.tinyioc.context;

import com.huang.tinyioc.BeanDefinition;
import com.huang.tinyioc.factory.AbstractBeanFactory;
import com.huang.tinyioc.factory.AutowiredBeanFactory;
import com.huang.tinyioc.io.ResourceLoader;
import com.huang.tinyioc.xml.XmlBeanDefinitionReader;

import java.util.Map;

public class ClassPathXmlApplicationContext extends AbstractApplicationContext{

    private String configLocation;

    public ClassPathXmlApplicationContext(AbstractBeanFactory beanFactory, String configLocation) throws Exception {
        super(beanFactory);
        this.configLocation = configLocation;
        InitializeBeanFactory();
    }

    public ClassPathXmlApplicationContext(String configLocation) throws Exception {
        this(new AutowiredBeanFactory(),configLocation);
    }

    @Override
    public void InitializeBeanFactory() throws Exception {
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        xmlBeanDefinitionReader.loadBeanDefinitions(configLocation);
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : xmlBeanDefinitionReader.getRegistry().entrySet()) {
            beanFactory.registerBeanDefination(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
        }
    }

    public void registerBeanDefination(String name, BeanDefinition beanDefinition) {

    }
}
