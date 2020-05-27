package com.huang.tinyioc;

import org.junit.Test;

public class BeanFactoryTest {

    @Test
    public void test() throws NoSuchFieldException {
        //应该从test出发

        //初始化beanFactory
        BeanFactory beanFactory = new AutowiredBeanFactory();

        //创建beanDefination
        BeanDefinition beanDefinition = new BeanDefinition();
        beanDefinition.setBeanClassName("com.huang.tinyioc.Konnichiha");  //1.设置beanClassName 2.获得相关联的Class对象

        //设置属性
        ProperiesList properiesList = new ProperiesList();
        properiesList.addPropertyValue(new PropertyValue("text","こんにちは！"));
        beanDefinition.setProperiesList(properiesList);

        //放入容器
        beanFactory.registerBeanDefination("konnichiha",beanDefinition); //Class对象的newInstance()方法创建此对象表示的类的一个新实例.并注入容器中

        //获取bean
        Konnichiha konnichiha = (Konnichiha) beanFactory.getBean("konnichiha");
        konnichiha.say();
    }

}

