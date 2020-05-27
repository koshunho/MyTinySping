# MyTinySping
In order to deeply understand the core idea of Spring

### Step 1: 创建BeanFactory

用一个<BeanName, BeanDefination>类型的ConcurrentHashMap来作为容器。

```java
public class BeanDefinition {
    private Object bean;

    public BeanDefinition(Object bean) {
        this.bean = bean;
    }

    public Object getBean() {
        return bean;
    }
}
```

### Step 2：使用容器来管理bean的创建
Step1中，我们是手动new一个bean的实例再放到容器中的。在这步我们改为由容器帮我们new对象的实例。

```java
//在BeanDefination中
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
```

```java
//在AutowiredBeanFactory中
    //第二步：再通过Class对象的newInstance()方法创建此对象表示的类的一个新实例，即通过一个类名字符串得到类的实例。
        protected Object createBean(BeanDefinition beanDefinition) {
        try {
            Object o = beanDefinition.getBeanClass().newInstance();
            return o;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
```

### Step 3：给bean赋值

应该是给bean注入属性才对。

在BeanDefination中增加一个字段ProperiesList properiesList
```java
//用一个List来保存自己申明的ProperyValue
public class ProperiesList {
    private final List<PropertyValue> list = new ArrayList<PropertyValue>();

    public void addPropertyValue(PropertyValue pv){
        list.add(pv);
    }

    public List<PropertyValue> getList() {
        return list;
    }
}

```

```java
//PropertyValue保存属性名和对应的值
public class PropertyValue {
    private final String fieldName;

    private final Object fieldValue;

    public PropertyValue(String fieldName, Object fieldValue) {
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Object getFieldValue() {
        return fieldValue;
    }
}
```
我们在实例化bean并放到beanFactory的时候，同时就给这个bean注入属性。从beanDefination的properiesList依次取出键值。
```java
    //getDeclaredField是可以获取一个类本身的所有字段.
    //getField只能获取类及其父类的public字段.
    protected void setPropertyValueToBean(Object bean, BeanDefinition beanDefinition) throws NoSuchFieldException, IllegalAccessException {
        for(PropertyValue propertyValue: beanDefinition.getProperiesList().getList()){
            Field declaredField = bean.getClass().getDeclaredField(propertyValue.getFieldName());
            declaredField.setAccessible(true);
            declaredField.set(bean, propertyValue.getFieldValue());
        }
    }
```
