package com.huang.tinyioc.xml;

import com.huang.tinyioc.BeanDefinition;
import com.huang.tinyioc.io.ResourceLoader;

import java.util.HashMap;
import java.util.Map;

//只能使用构造器注入
public abstract class AbstractBeanDefinitionReader implements BeanDefinationReader {

    private Map<String, BeanDefinition> registry;

    private ResourceLoader resourceLoader;

    public AbstractBeanDefinitionReader(ResourceLoader resourceLoader) {
        this.registry = new HashMap<String, BeanDefinition>();
        this.resourceLoader = resourceLoader;
    }

    public Map<String, BeanDefinition> getRegistry() {
        return registry;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
