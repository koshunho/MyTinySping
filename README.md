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
### Step 4：读取xml配置来初始化bean
1.初始化IO配置

URL类定位xml文件，url.openConnect().connect()即可定位并打开文件，利用getInputStream获得文件输入流。

2.读取xml本地文件

通过XMLBeanDefinitionReader类和DocumentBuilder对xml进行解析。先根据bean定位到所有的bean。

##### XMLBeanDefinitionReader的流程：获取inputStream-->解析inputStream为document-->从document中解析出bean并进行注册相关操作-->遍历逐项进行注册-->单个进行注册-->将数据存在PropertiesList中后赋值给每个属性

3.通过之前已有的操作进行实例化已经属性初始化

根据类名和实例名构建一个空实例，然后每一个bean中定位property，利用PropertiesList类和PV类实现对bean属性的赋值。

#### 这一步中实操中遇到的问题
1.最先是在写配置文件时，我用来测试注入的实体类Konnichiha怎么也都无法被识别出来。Konnichiha类位于test下，跟main中是同包的，在XML中写类的全限定名的时候也有提示，但是还是一直爆红。查看Target中也没有这个类，感觉很奇怪。

最后发现是在Module Setting中，没有把test设置为Test Resources。。。最后加进去就能读出来了。

导入命名空间的时候也一直爆红，不过不影响使用，忽略掉就好了。

2.这个问题就有些隐蔽了。在测试XmlBeanDefinitionReaderTest的时候XmlBeanDefinitionReader.processProperty(Element element, BeanDefinition beanDefinition)方法在beanDefinition.getPropertiesList().addPropertyValue(new PropertyValue(name,value))步骤一直报空指针，百思不得其解，测了IO也能正常读。

排错了好久，发现原因是传递进来的beanDefinition在上个方法中只是new出来的只有BeanDefinition beanDefinition = new BeanDefinition()，所以beanDefinition.getPropertiesList()其实是null，在addPropertyValue的时候会报空指针。

两个解决办法：

1. 在BeanDefination中 private ProperiesList properiesList = new ProperiesList(); 这样创建一个BeanDefination的实例的时候也就创建了一个ProperiesList

2.在processProperty方法体中自己new一个ProperiesList，再set进上面方法传进来的beanDefinition中
