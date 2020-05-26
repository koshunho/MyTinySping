package com.huang.tinyioc;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

//最基本的容器
public class BeanFactory {
    private Map<String, BeanDefinition> map = new ConcurrentHashMap<String, BeanDefinition>();

    public Object getBean(String beanName){
        return map.get(beanName).getBean();
    }

    public void registerBean(String beanName, BeanDefinition beanDefinition){
        map.put(beanName,beanDefinition);
    }
}
