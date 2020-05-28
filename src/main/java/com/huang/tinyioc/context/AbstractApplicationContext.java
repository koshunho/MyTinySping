package com.huang.tinyioc.context;

import com.huang.tinyioc.factory.AbstractBeanFactory;

public abstract class AbstractApplicationContext implements ApplicationContext{
    protected AbstractBeanFactory beanFactory;

    public AbstractApplicationContext(AbstractBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public void InitializeBeanFactory() throws Exception {
    }

    public Object getBean(String name) throws NoSuchFieldException {
        return beanFactory.getBean(name);
    }
}
