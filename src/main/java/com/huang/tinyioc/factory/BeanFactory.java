package com.huang.tinyioc.factory;

import com.huang.tinyioc.BeanDefinition;

public interface BeanFactory {
    Object getBean(String name) throws NoSuchFieldException;

    void registerBeanDefination(String name, BeanDefinition beanDefinition) throws NoSuchFieldException;

}

