package com.huang.tinyioc.aop;

import com.huang.tinyioc.Konnichiha;
import com.huang.tinyioc.KonnichihaImpl;
import com.huang.tinyioc.context.ApplicationContext;
import com.huang.tinyioc.context.ClassPathXmlApplicationContext;
import org.junit.Test;

public class ProxyTest {
    @Test
    public void testProxy() throws Exception {
        //先创建一个业务实现类对象(Impl) 和 一个代理类对象
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("MyTinySpring.xml");
        //真实对象！Impl!
        LogInterceptor logInterceptor = new LogInterceptor();
        KonnichihaImpl konnichiha = (KonnichihaImpl) applicationContext.getBean("konnichiha");
        ProxyInvocationHandler pih = new ProxyInvocationHandler();
        pih.setTarget(konnichiha);
        pih.setMethodInterceptor(logInterceptor);
        Konnichiha proxy = (Konnichiha)pih.getProxy();
        proxy.say();
    }
}
