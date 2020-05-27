package com.huang.tinyioc;

public class AutowiredBeanFactory extends AbstractBeanFactory{
    protected Object createBean(BeanDefinition beanDefinition) {
        try {
            Object o = beanDefinition.getBeanClass().newInstance();
            return o;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
