package com.huang.tinyioc.factory;

import com.huang.tinyioc.BeanDefinition;
import com.huang.tinyioc.factory.BeanFactory;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractBeanFactory implements BeanFactory {

    //容器
    private Map<String, BeanDefinition> map = new HashMap<String, BeanDefinition>();

    public Object getBean(String name) {
        return map.get(name).getBean();
    }

    public void registerBeanDefination(String name, BeanDefinition beanDefinition) throws NoSuchFieldException {
        Object bean = createBean(beanDefinition); //实例化
        beanDefinition.setBean(bean);
        map.put(name, beanDefinition);
    }
    //第二步：再通过Class对象的newInstance()方法创建此对象表示的类的一个新实例，即通过一个类名字符串得到类的实例。
    protected abstract Object createBean(BeanDefinition beanDefinition) throws NoSuchFieldException;
}
