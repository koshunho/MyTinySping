package com.huang.tinyioc;

//没有有参构造，全部通过set()方法注入属性
public class BeanDefinition {
    private Object bean;

    private String beanClassName;

    private Class beanClass;

    private ProperiesList properiesList;

    public BeanDefinition() {
    }

    public String getBeanClassName() {
        return beanClassName;
    }
    // 反射创建一个实例化对象：两步
    // 第一步：Class.forName(String className)方法可以返回与带有给定字符串名的类或接口相关联的Class对象。
    // 第二步：再通过Class对象的newInstance()方法创建此对象表示的类的一个新实例，即通过一个类名字符串得到类的实例。
    // 这里为第一步。设置beanClassName的同时也设置了beanClass
    public void setBeanClassName(String beanClassName) {
        this.beanClassName = beanClassName;
        try{
            this.beanClass = Class.forName(beanClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Class getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class beanClass) {
        this.beanClass = beanClass;
    }


    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Object getBean() {
        return bean;
    }

    public ProperiesList getProperiesList() {
        return properiesList;
    }

    public void setProperiesList(ProperiesList properiesList) {
        this.properiesList = properiesList;
    }
}
