package com.huang.tinyioc;

import org.junit.Test;

public class BeanFactoryTest {

    @Test
    public void test(){
        //初始化beanFactory
        BeanFactory beanFactory = new BeanFactory();

        //注入bean
        BeanDefinition beanDefinition = new BeanDefinition(new Konnichiha());
        beanFactory.registerBean("konnichiha", beanDefinition);

        Konnichiha konnichiha = (Konnichiha) beanFactory.getBean("konnichiha");
        konnichiha.sayKonnichiha();
    }

}

