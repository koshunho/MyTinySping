package com.huang.tinyioc;

import com.huang.tinyioc.factory.AutowiredBeanFactory;
import com.huang.tinyioc.factory.BeanFactory;
import com.huang.tinyioc.io.ResourceLoader;
import com.huang.tinyioc.xml.XmlBeanDefinitionReader;
import org.junit.Test;

import java.util.Map;

public class BeanFactoryTest {

    @Test
    public void test() throws Exception {
        //应该从test出发

        // 1.读取配置
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        xmlBeanDefinitionReader.loadBeanDefinitions("MyTinySpring.xml");

        // 2.初始化BeanFactory并注册bean
        BeanFactory beanFactory = new AutowiredBeanFactory();
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : xmlBeanDefinitionReader.getRegistry().entrySet()) {
            beanFactory.registerBeanDefination(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
        }

        // 3.获取bean
        Konnichiha konnichiha = (Konnichiha) beanFactory.getBean("konnichiha");
        konnichiha.say();

    }

}

