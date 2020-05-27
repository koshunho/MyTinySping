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
