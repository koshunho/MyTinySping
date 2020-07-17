# MyTinySping

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

这里说一下面向接口和面向抽象类编程的区别。

面向接口变成就是你要实现接口中【所有所有】的方法，管你用不用得上；但是，假如有个抽象类去实现了这个接口（抽象类里面都是空方法，还可以自己添加新的方法），然后我们只需要去继承这个抽象类，重写其中我们需要的方法就可以了，用多少，就重写多少。

举例，JUI的KeyAdapter就是一个抽象类，它实现了KeyListener接口。如果我们直接实现KeyListener接口，需要实现接口中所有方法。而KeyAdapter是一个抽象类，对所有可能的事件提供了空实现。我们通过继承这个抽象类，然后对自己所关心的方法进行重写就好，不必理会其他方法。
```java
public interface KeyListener extends EventListener {

    public void keyTyped(KeyEvent e);

    public void keyPressed(KeyEvent e);

    public void keyReleased(KeyEvent e);
}
```
注释解释得非常清楚了。

Extend this class to create a <code>KeyEvent</code> listenerand override the methods for the events of interest. 

(If you implement the <code>KeyListener</code> interface, you have to define all of the methods in it. 

This abstract class defines null methods for them all, **so you can only have to define methods for events you care about.**)

```java
/**
 * An abstract adapter class for receiving keyboard events.
 * The methods in this class are empty. This class exists as
 * convenience for creating listener objects.
 * <P>
 * Extend this class to create a <code>KeyEvent</code> listener
 * and override the methods for the events of interest. (If you implement the
 * <code>KeyListener</code> interface, you have to define all of
 * the methods in it. This abstract class defines null methods for them
 * all, so you can only have to define methods for events you care about.)
 * <P>
 * Create a listener object using the extended class and then register it with
 * a component using the component's <code>addKeyListener</code>
 * method. When a key is pressed, released, or typed,
 * the relevant method in the listener object is invoked,
 * and the <code>KeyEvent</code> is passed to it.
 */
public abstract class KeyAdapter implements KeyListener {
    public void keyTyped(KeyEvent e) {}

    public void keyPressed(KeyEvent e) {}

    public void keyReleased(KeyEvent e) {}
}
```

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
本项目大量运用这样的思想，保证拓展性。随着类层次变多，每一层需要处理的逻辑会减少，职责更加明确和单一，不论是编写还是理解都会更加容易。

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
定义BeanDefinitionReader接口，提供void loadBeanDefinitions(String location)方法从指定位置获取相应的配置来进行初始化，定义抽象类AbstractBeanDefinitionReader,其中一个实现是XmlBeanDefinitionReader。
```java
public interface BeanDefinitionReader {
    void loadBeanDefinitions(String location) throws Exception;
}
```

```java
public abstract class AbstractBeanDefinitionReader implements BeanDefinationReader {
    
    // 保存从配置文件中加载的所有的 beanDefinition 对象
    private Map<String, BeanDefinition> registry;

    /**
     * 依赖 ResourceLoader，该类又依赖 UrlResource 
     * UrlResource 继承自 Spring 自带的 Resource 内部资源定位接口
     * Resource 接口，标识一个外部资源。通过 getInputStream() 方法 获取资源的输入流 。
     * UrlResource 实现 Resource 接口的资源类，通过 URL 获取资源。
     * ResourceLoader 资源加载类。通过 getResource(String) 方法获取一个 Resource 对象，是获取 Resource 的主要途径.
     */
    private ResourceLoader resourceLoader;

    public AbstractBeanDefinitionReader(ResourceLoader resourceLoader) {
        this.registry = new HashMap<String, BeanDefinition>();
        this.resourceLoader = resourceLoader;
    }

    public Map<String, BeanDefinition> getRegistry() {
        return registry;
    }

    public ResourceLoader getResourceLoader() {
        return resourceLoader;
    }
}
```

```java
public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader{
    public XmlBeanDefinitionReader(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    //获取inputStream，调用doLoadBeanDefinitions()方法
    public void loadBeanDefinitions(String location) throws Exception {
        InputStream inputStream = getResourceLoader().getResource(location).getInputStream();
        doLoadBeanDefinitions(inputStream);
    }

    //解析inputStream为doc，调用registerBeanDefinitions(doc)
    protected void doLoadBeanDefinitions(InputStream inputStream) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = factory.newDocumentBuilder();
        // 输入流转换成对应的 Document 对象便于获取对应的元素
        Document doc = docBuilder.parse(inputStream);
        // 解析bean
        registerBeanDefinitions(doc);
        inputStream.close();
    }

    //进行bean注册相关操作，调用parseBeanDefinitions(root)
    public void registerBeanDefinitions(Document doc) {
        // 获取文件中包含的元素 <beans></beans>。是beans!!!beans!!!!beans!!! 不是bean，下面才是
        Element root = doc.getDocumentElement();

        parseBeanDefinitions(root);
    }
    //遍历逐项进行注册
    protected void parseBeanDefinitions(Element root) {
        // 获取元素包含的子节点链 <bean></bean> 链。 
        NodeList nl = root.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            // 获取子节点链上对应的子节点 单个<bean></bean>
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element ele = (Element) node;
                // 将节点强转为 Element 对象并进行解析
                processBeanDefinition(ele);
            }
        }
    }
    //单个进行注册
    protected void processBeanDefinition(Element ele) {
        // 获取子节点对应的属性（name，class）来对应 Bean
        // <bean name="" class=""></bean>
        String name = ele.getAttribute("name");
        String className = ele.getAttribute("class");
        // 创建与之对应的 BeanDefinition ，并设置相应的属性
        BeanDefinition beanDefinition = new BeanDefinition();
        // 将节点包含的 Bean相关的属性信息注入创建的 BeanDefinition 中。
        processProperty(ele,beanDefinition);
        beanDefinition.setBeanClassName(className);
        // 统一管理（HashMap）通过配置文件加载的 beanDefinition 对象
        getRegistry().put(name, beanDefinition);
    }

    //为bean注入bean。
/*    假设xml文件是这么写的。有一个Bean
    <bean name="helloWorldService" class="us.codecraft.tinyioc.HelloWorldService">
        <property name="text" value="Hello World!"></property>
        <property name="outputService" ref="outputService"></property>
    </bean>*/
    //所以判断当前property申明的是简单类型的话就看value!=null
    //是一个ref的话那说明value == null，因为就没有value嘛是ref
    private void processProperty(Element ele,BeanDefinition beanDefinition) {
        // 获取元素对应的 Property 节点
        // <bean><property name="" value=""></property></bean>
        NodeList propertyNode = ele.getElementsByTagName("property");

        ProperiesList properiesList = new ProperiesList();

        for (int i = 0; i < propertyNode.getLength(); i++) {
            // 遍历节点并取出节点对应的 key-value，添加到 BeanDefinition 对应的属性中
            Node node = propertyNode.item(i);
            if (node instanceof Element) {
                Element propertyEle = (Element) node;
                String name = propertyEle.getAttribute("name");
                String value = propertyEle.getAttribute("value");
                if (value != null && value.length() > 0) {
                    properiesList.addPropertyValue(new PropertyValue(name, value));
                } else {
                    String ref = propertyEle.getAttribute("ref");
                    if (ref == null || ref.length() == 0) {
                        throw new IllegalArgumentException("Configuration problem: <property> element for property '"
                                + name + "' must specify a ref or value");
                    }
                    BeanReference beanReference = new BeanReference(ref);
                    properiesList.addPropertyValue(new PropertyValue(name, beanReference));
                }
            }
        }

        beanDefinition.setProperiesList(properiesList);
    }
}
```
　　1.初始化IO配置

　　URL类定位xml文件，url.openConnect().connect()即可定位并打开文件，利用getInputStream获得文件输入流。

　　2.读取xml本地文件

　　通过XMLBeanDefinitionReader类和DocumentBuilder对xml进行解析。先根据bean定位到所有的bean。

##### XMLBeanDefinitionReader的流程：获取inputStream-->解析inputStream为document-->从document中解析出bean并进行注册相关操作-->遍历逐项进行注册-->单个进行注册-->将数据存在PropertiesList中后赋值给每个属性

　　3.通过之前已有的操作进行实例化已经属性初始化

　　根据类名和实例名构建一个空实例，然后每一个bean中定位property，利用PropertiesList类和PV类实现对bean属性的赋值。

#### 这一步中实操中遇到的问题
1. 最先是在写配置文件时，我用来测试注入的实体类Konnichiha怎么也都无法被识别出来。Konnichiha类位于test下，跟main中是同包的，在XML中写类的全限定名的时候也有提示，但是还是一直爆红。查看Target中也没有这个类，感觉很奇怪。

　　最后发现是在Module Setting中，没有把test设置为Test Resources。。。最后加进去就能读出来了。

　　导入命名空间的时候也一直爆红，不过不影响使用，忽略掉就好了。

2. 这个问题就有些隐蔽了。在测试XmlBeanDefinitionReaderTest的时候XmlBeanDefinitionReader.processProperty(Element element, BeanDefinition beanDefinition)方法在beanDefinition.getPropertiesList().addPropertyValue(new PropertyValue(name,value))步骤一直报空指针，百思不得其解，测了IO也能正常读。

　　排错了好久，发现原因是传递进来的beanDefinition在上个方法中只是new出来的只有BeanDefinition beanDefinition = new BeanDefinition()，所以beanDefinition.getPropertiesList()其实是null，在addPropertyValue的时候会报空指针。

两个解决办法：

1. 在BeanDefination中 private ProperiesList properiesList = new ProperiesList(); 这样创建一个BeanDefination的实例的时候也就创建了一个ProperiesList

2. 在processProperty方法体中自己new一个ProperiesList，再set进上面方法传进来的beanDefinition中

### Step 5：bean注入bean
Step4只是完成了简单类型的注入，但是没有处理bean之间的依赖。

定义一个类叫BeanReference。

然后在XmlBeanDefinationReader中，最后一步给属性赋值的时候就要判断一下。
```java
    //为bean注入bean。
/*    假设xml文件是这么写的。有一个Bean
    <bean name="helloWorldService" class="us.codecraft.tinyioc.HelloWorldService">
        <property name="text" value="Hello World!"></property>
        <property name="outputService" ref="outputService"></property>
    </bean>*/
    //所以判断当前property申明的是简单类型的话就看value!=null
    //是一个ref的话那说明value == null，因为就没有value嘛是ref
    private void processProperty(Element ele,BeanDefinition beanDefinition) {
        NodeList propertyNode = ele.getElementsByTagName("property");

        ProperiesList properiesList = new ProperiesList();

        for (int i = 0; i < propertyNode.getLength(); i++) {
            Node node = propertyNode.item(i);
            if (node instanceof Element) {
                Element propertyEle = (Element) node;
                String name = propertyEle.getAttribute("name");
                String value = propertyEle.getAttribute("value");
                if (value != null && value.length() > 0) {
                    properiesList.addPropertyValue(new PropertyValue(name, value));
                } else {
                    String ref = propertyEle.getAttribute("ref");
                    if (ref == null || ref.length() == 0) {
                        throw new IllegalArgumentException;
                    }
                    BeanReference beanReference = new BeanReference(ref);
                    properiesList.addPropertyValue(new PropertyValue(name, beanReference));
                }
            }
        }
        beanDefinition.setProperiesList(properiesList);
    }
```
最后仍然是存到这个beanDefition的ProperiesList中，等待后面实例化的时候再对bean赋值。

#### 怎么避免循环依赖？？？
之前我们的BeanFactory，在register bean的时候就创建出bean的实例保存到对应的beanDefinition了。

为了避免循环依赖，我们就在getBean的时候才创建出bean。

所谓循环依赖，就是
```java
    <bean name="nihao" class="com.huang.tinyioc.Nihao">
        <property name="konnichihaImpl" ref="konnichihaImpl"/>
    </bean>

    <bean name="konnichihaImpl" class="com.huang.tinyioc.KonnichihaImpl">
        <property name="text" value="こんにちは！"></property>
        <property name="nihao" ref="nihao"></property>
    </bean>
```
在createBean()方法中，创建完空的bean(空的bean表示空构造函数构造出的bean)后，就放入beanDefinition中，这样a ref b，b ref a时，a ref b因此b先创建并指向a，此时的a还不是完全体，但是引用已经连上了，然后创建好了b。然后b ref a的时候，a已经创建完毕。

**再理解**:打个比方，像是做饭炒菜。之前都是准备好材料（把相关信息从xml读好后写进XMLReader的registry），然后就直接开始炒了（registerBeanDefination就直接实例化并设置进beanFactory）。

而现在是先准备好材料（BeanDefinition），我等客人点A菜，我才开始炒A菜。如果A菜的构成需要B菜，B菜的构成却需要A菜，注意，客人点的是A菜。在炒菜的时候（也就是指createBean()方法），我先生成A菜的实例，然后马上放入beanFacotory。

然后我才开始为A菜赋值，发现A菜的构成需要B菜。那么我来处理B菜，也开始炒B菜（也就是调用createBean()方法），却发现B菜的构成需要A菜，我就去找A菜，咦，发现有A菜的实例了（**即使A菜此时还不是完全体**），这样我就能炒好B菜了。

于是B菜返回给A菜，A菜就进化成完全体了，嘻嘻。

createBean()个人理解，就是为BeanDefinition这些材料注入灵魂，令它成为真正的完全体。

最妙的就是这里。
```java
protected Object createBean(BeanDefinition beanDefinition) throws NoSuchFieldException {
        try {
            //实例化
            Object o = beanDefinition.getBeanClass().newInstance();

            //step5 registerBeanDefination就setBean() 改为 在这里setBean()!!!
            //创建出新的实例之后就setBean，不等赋值再放。是因为！！！！
            //创建完空的bean(空的bean表示空构造函数构造出的bean)后，就放入beanDefinition中，
            //这样假设有循环依赖 a ref b，b ref a时，a ref b。因此b先创建并指向a，此时的a还不是完全体，但是引用已经连上了
            //然后创建好了b。然后b ref a的时候，a已经创建完毕。
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
```
### Step 6：引入ApplicationContext接口
我们正常使用Spring获取bean应该是这样的：
```java
    @Test
    public void test() throws Exception {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("MyTinySpring.xml");
        Konnichiha konnichiha = (Konnichiha) applicationContext.getBean("konnichiha");
        konnichiha.say();
    }
```
而不应该是这样的：
```java
    @Test
    public void test() throws Exception {
        //应该从test出发

        // 1.读取配置
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader(new ResourceLoader());
        xmlBeanDefinitionReader.loadBeanDefinitions("MyTinySpring.xml");

        // 2.初始化BeanFactory
        BeanFactory beanFactory = new AutowiredBeanFactory();
        for (Map.Entry<String, BeanDefinition> beanDefinitionEntry : xmlBeanDefinitionReader.getRegistry().entrySet()) {
            beanFactory.registerBeanDefination(beanDefinitionEntry.getKey(), beanDefinitionEntry.getValue());
        }

        // 3.注册bean并获取bean
        Konnichiha konnichiha = (Konnichiha) beanFactory.getBean("konnichiha");
        konnichiha.say();

    }
```
Step 6就是将这些函数全部整合进行一个context包中以后就只需要调用一个简单的函数即可，无需关注其他的函数。

### Step 7：动态代理实现AOP织入
先写一个通用的动态代理实现的类，所有的代理对象设置为Object即可。

JDK的动态代理需要了解2个类：InvocationHandler、Proxy

```java
//invoke()：当我们通过动态代理对象调用一个方法的时候，这个方法的调用就会被 转 发 到实现InvocationHandler接口的invoke()方法 
public interface invocationHandler{
   //参数： proxy：调用该方法的代理实例
   //method: 对应于调用代理实例上的接口方法的实例
   //args:包含的方法调用传递代理实例的参数值的对象的阵列
   public Object invoke(Object proxy, Methoid method, Object[] args) throw Throwable;
}
```
```java
public class Proxy{
...
protected InvocationHandler h;
...
//loader:类加载器来定义代理类
//interfaces:代理类实现的 接 口 列表。 接 口  接 口  接 口  接 口  接 口  接 口  接 口  接 口 
//h:调度方法调用的调用处理函数
public static Object newProxyInstance(ClassLoader loader, class<?>[] interfaces, InvocationHandler h){...}
}
```

现在先不管连接点、advice那些，先来试试看自己写的。
```java
public class ProxyInvocationHandler implements InvocationHandler {
    private Object target;

    public void setTarget(Object target) {
        this.target = target;
    }

    //生成代理类
    public Object getProxy(){
        return Proxy.newProxyInstance(this.getClass().getClassLoader(),target.getClass().getInterfaces(),this);
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        before(method.getName());
        Object result = method.invoke(target, args);
        after(method.getName());
        return result;
    }

    public void before(String methodName){
        System.out.println("开始执行"+methodName+"方法！");
    }

    public void after(String methodName){
        System.out.println(methodName+"方法执行完毕！");
    }
}
```
然后调用测试看看。发现方法被增强了
```java
public class ProxyTest {
    @Test
    public void testProxy() throws Exception {
        //先创建一个业务实现类对象(Impl) 和 一个代理类对象
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("MyTinySpring.xml");
        //真实对象！Impl!
        KonnichihaImpl konnichiha = (KonnichihaImpl) applicationContext.getBean("konnichiha");
        ProxyInvocationHandler pih = new ProxyInvocationHandler();
        pih.setTarget(konnichiha);
        Konnichiha proxy = (Konnichiha)pih.getProxy();
        proxy.say();
    }
}
```
但是before()和after()方法是我们手写的，这个应该交给用户来写吧？

#### MethodInterceptor和MethodInvocation
这两个角色都是AOP联盟的标准，它们分别对应AOP中两个基本角色：Advice和Joinpoint。Advice定义了在切点指定的逻辑，而Joinpoint则代表切点。

##### Advice（增强！！！！！！！）：
  　由 aspect 添加到特定的 join point(即满足 point cut 规则的 join point) 的一段代码。
   
　　许多 AOP框架会将 advice 模拟为一个拦截器(interceptor), 并且在 join point 上维护多个 advice, 进行层层拦截。
  
　　1. Before：在被代理方法执行之前执行，它不能控制被代理方法的执行与否
  
　　2. After returning：在被代理方法正常return之后执行
  
　　3. After throwing：在被代理方法抛出异常后执行
  
　　4. After (finally)：在上述两种情况（正常return或抛出异常）之后执行
  
　　5. Around：在被代理方法前后执行。只有该方法才能控制被代理方法执行与否（）

##### Joinpoint：方法的执行点！！！！！！！！
　　有返回值的@Advice方法，你需要主动通过return JoinPoint.proceed()才可以得到被代理方法的原始返回值，如果直接return 其他值并且不调用JoinPoint.proceed()，那么被代理方法将直接被忽略不执行。
  
  <div align=center><img src="https://s1.ax1x.com/2020/05/30/tMIYh8.png"/></div> 
  
　　这个图解释得很好。连接点和切点的概念我有些弄不清，看到一个例子：比如开车经过一条高速公路，这条高速公路上有很多个出口（连接点），但是我们不会每个出口都会出去，只会选择我们需要的那个出口（切点）开出去。

　　简单可以理解为，每个出口都是连接点，但是我们使用的那个出口才是切点。每个应用有多个位置适合织入通知，这些位置都是连接点。但是只有我们选择的那个具体的位置才是切点。
  
在ProxyInvocationHandler里，我们只需要将MethodInterceptor放入对象的方法调用即可。

导入aopalliance包。
```java
public class MethodInvocationImpl implements MethodInvocation {
    private Object target;

    private Method method;

    private Object[] args;

    public MethodInvocationImpl(Object target, Method method, Object[] args) {
        this.target = target;
        this.method = method;
        this.args = args;
    }

    public Method getMethod() {
        return method;
    }

    public Object[] getArguments() {
        return args;
    }

    // 反射，相当于 target.method(args);即调用代理对象对应的方法
    public Object proceed() throws Throwable {
        return method.invoke(target, args);
    }

    public Object getThis() {
        return target;
    }

    public AccessibleObject getStaticPart() {
        return method;
    }
}
```

```java
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
    
    //重写 InvocationHandler 对应的 invoke() 方法
    //调用拦截器对应的方法
    //通过反射获取对应的切点，再根据切点指定的逻辑进行执行
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        //这里需要一个MethodInvocation类型的参数，那么我们创建看看
        return methodInterceptor.invoke(new MethodInvocationImpl(target,method,args));
    }
}
```

说了那么多我们来测试一下。

首先定义方法的增强advice，也就是MethodInterceptor。正如之前所说，需要主动通过return JoinPoint.proceed()才可以得到被代理方法的原始返回值
```java
public class LogInterceptor implements MethodInterceptor {

    //MethodInvocation相当于方法的执行点！！！！！！！！JoinPoint
    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {
        long time = System.nanoTime();
        
        System.out.println("Invocation of Method " + invocation.getMethod().getName() + " start!");
        
        Object proceed = invocation.proceed();
        
        System.out.println("Invocation of Method " + invocation.getMethod().getName() + " end! takes " + (System.nanoTime() - time)
                + " nanoseconds.");
                
        return proceed;
    }
}
```
然后把方法的增加set进代理类。
```java
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
```

### Step 7.1：动态代理实现AOP织入(补充Step 4)
使用Xpath。

望文生义 --> XML + 路径

**注意**：Xpath在读取Spring配置文件时无法通过selectNodes或selectSingleNode获取节点，是因为配置文件带有命名空间。

### Step 8：AspectJ管理切面
使用AspectJ管理切面。

Step 7解决了怎么织入的问题，下面就是在哪里织入？Spring采用了AspectJ风格的标示性语句来表示在哪些位置进行织入，即哪些位置是point cut。类似下面的语句<aop:pointcut id="pointcut" expression="execution(public int aopxml.Calculator.*(int, int ))"/>。Spring可以对类和方法做插入，因此我们也要实现对类和方法表示point cut的功能。

在AspectJExpressionPointcut中，
1. 获得String expression即AspectJ风格表达式
2. 创建PonitcutParser，即解析AspectJ风格表达式的解析器。
3. expression被解析后就变成了pointcutExpression。即expression是输入，pointcutParser是输出，pointcutParser是解析器，将输入解析成输出。

这个解析器怎么创建呢？直接new一个行不行啊？不行。

正确的创建方式为：pointcutParser = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingContextClassloaderForResolution(supportedPrimitives);

后面的supportedPrimitives指的是执行的AspectJ语言风格的关键字，是一个set

pointcutExpression是创建好了，但是有什么用呢？这个类可以用于匹配方法和类。

```java
//匹配类
pointcutExpression.couldMatchJoinPointsInType(targetClass);
//匹配方法
ShadowMatch shadowMatch = pointcutExpression.matchesMethodExecution(method);

```

### Step 9：将AOP融入Bean的创建过程

这个好嗨难 待更