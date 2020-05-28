package com.huang.tinyioc.factory;

import com.huang.tinyioc.BeanDefinition;
import com.huang.tinyioc.BeanReference;
import com.huang.tinyioc.PropertyValue;

import java.lang.reflect.Field;

public class AutowiredBeanFactory extends AbstractBeanFactory {
    protected Object createBean(BeanDefinition beanDefinition) throws NoSuchFieldException {
        try {
            //实例化
            Object o = beanDefinition.getBeanClass().newInstance();

            //step5 registerBeanDefination就setBean() 改为 在这里setBean()!!!
            // 创建出新的实例之后就setBean，不等赋值再放。是因为！！！！
            //创建完空的bean(空的bean表示空构造函数构造出的bean)后，就放入beanDefinition中，
            // 这样假设有循环依赖 a ref b，b ref a时，a ref b。因此b先创建并指向a，此时的a还不是完全体，但是引用已经连上了，
            // 然后创建好了b。然后b ref a的时候，a已经创建完毕。
            beanDefinition.setBean(o);
            //5555太妙了吧！！！！

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
            Object value = propertyValue.getFieldValue();
            //如果value是ref的话，就从BeanFactory中找到这个bean，再装配上
            if(value instanceof BeanReference){
                BeanReference beanReference = (BeanReference)value;
                value = getBean(beanReference.getName()); //懒加载
            }
            declaredField.set(bean,value);
        }
    }
}
