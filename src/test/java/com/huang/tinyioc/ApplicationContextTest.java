package com.huang.tinyioc;

import com.huang.tinyioc.context.ApplicationContext;
import com.huang.tinyioc.context.ClassPathXmlApplicationContext;
import org.junit.Test;

public class ApplicationContextTest {
    @Test
    public void test() throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("MyTinySpring.xml");
        Konnichiha konnichiha = (Konnichiha) applicationContext.getBean("konnichiha");
        konnichiha.say();
    }
}
