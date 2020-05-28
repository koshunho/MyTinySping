package com.huang.tinyioc.factory;

import com.huang.tinyioc.BeanDefinition;
import com.huang.tinyioc.factory.BeanFactory;

import java.util.*;

public abstract class AbstractBeanFactory implements BeanFactory {

    //容器
    private Map<String, BeanDefinition> map = new HashMap<String, BeanDefinition>();

    private final List<String> beanDefinitionNames = new ArrayList<String>();

    //妙啊！
    public Object getBean(String name) throws NoSuchFieldException {
        //return map.get(name).getBean();
        BeanDefinition beanDefinition = map.get(name);
        if (beanDefinition == null) {
            throw new IllegalArgumentException("No bean named " + name + " is defined");
        }
        Object bean = beanDefinition.getBean();     //懒加载!!!!!!bean的实例化放在获取getBean()时才执行。之前都是在registerBeanDefination中执行的。
        if (bean == null) {
            bean = createBean(beanDefinition);
        }
        return bean;
    }

    public void registerBeanDefination(String name, BeanDefinition beanDefinition) throws NoSuchFieldException {
        //不再是registerBeanDefination时就实例化，而是放在getBean的时候才实例化，实现了懒加载！！
        /*        Object bean = createBean(beanDefinition); //实例化
        beanDefinition.setBean(bean);*/
        map.put(name, beanDefinition);

        beanDefinitionNames.add(name);
    }

    //开始实例化的入口
    public void preInstantiateSingletons() throws Exception {
        for (Iterator it = this.beanDefinitionNames.iterator(); it.hasNext();) {
            String beanName = (String) it.next();
            getBean(beanName);
        }
    }

    //第二步：再通过Class对象的newInstance()方法创建此对象表示的类的一个新实例，即通过一个类名字符串得到类的实例。
    protected abstract Object createBean(BeanDefinition beanDefinition) throws NoSuchFieldException;
}
