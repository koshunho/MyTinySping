package com.huang.tinyioc.factory;

import com.huang.tinyioc.BeanDefinition;

public interface BeanFactory {
    Object getBean(String name);

    void registerBeanDefination(String name, BeanDefinition beanDefinition) throws NoSuchFieldException;

}

