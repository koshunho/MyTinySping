package com.huang.tinyioc.aop;

import org.aopalliance.intercept.MethodInterceptor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class ProxyInvocationHandler implements InvocationHandler {
    private Object target;

    //MethodInterceptor相当于AOP的advice
    private MethodInterceptor methodInterceptor;

    public void setMethodInterceptor(MethodInterceptor methodInterceptor) {
        this.methodInterceptor = methodInterceptor;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    //生成代理类
    public Object getProxy(){
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),target.getClass().getInterfaces(),this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //before(method.getName());
        //Object result = method.invoke(target, args);
        //after(method.getName());

        //这里需要一个MethodInvocation类型的参数，那么我们创建看看
        return methodInterceptor.invoke(new MethodInvocationImpl(target,method,args));
    }

    public void before(String methodName){
        System.out.println("开始执行"+methodName+"方法！");
    }

    public void after(String methodName){
        System.out.println(methodName+"方法执行完毕！");
    }
}
