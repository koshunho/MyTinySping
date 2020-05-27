package com.huang.tinyioc;

public interface BeanFactory {
    Object getBean(String name);

    void registerBeanDefination(String name, BeanDefinition beanDefinition) throws NoSuchFieldException;

}

