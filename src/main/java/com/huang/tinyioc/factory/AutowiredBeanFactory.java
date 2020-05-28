package com.huang.tinyioc.factory;

import com.huang.tinyioc.BeanDefinition;
import com.huang.tinyioc.PropertyValue;

import java.lang.reflect.Field;

public class AutowiredBeanFactory extends AbstractBeanFactory {
    protected Object createBean(BeanDefinition beanDefinition) throws NoSuchFieldException {
        try {
            //实例化
            Object o = beanDefinition.getBeanClass().newInstance();
            //赋值
            setPropertyValueToBean(o, beanDefinition);
            return o;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    //getDeclaredField是可以获取一个类本身的所有字段.
    //getField只能获取类及其父类的public字段.
    protected void setPropertyValueToBean(Object bean, BeanDefinition beanDefinition) throws NoSuchFieldException, IllegalAccessException {
        for(PropertyValue propertyValue: beanDefinition.getProperiesList().getList()){
            Field declaredField = bean.getClass().getDeclaredField(propertyValue.getFieldName());
            declaredField.setAccessible(true);
            declaredField.set(bean, propertyValue.getFieldValue());
        }
    }
}
