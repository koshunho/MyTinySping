package com.huang.tinyioc.xml;

import com.huang.tinyioc.BeanDefinition;
import com.huang.tinyioc.BeanReference;
import com.huang.tinyioc.ProperiesList;
import com.huang.tinyioc.PropertyValue;
import com.huang.tinyioc.io.ResourceLoader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

//通过XMLBeanDefinitionReader类和DocumentBuilder对xml进行解析。
// 先根据bean定位到所有的bean，根据类名和实例名构建一个空实例，
// 然后每一个bean中定位property，利用ProperiesList类和PV类实现对bean属性的赋值

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
        Document doc = docBuilder.parse(inputStream);
        // 解析bean
        registerBeanDefinitions(doc);
        inputStream.close();
    }

    //进行bean注册相关操作，调用parseBeanDefinitions(root)
    public void registerBeanDefinitions(Document doc) {
        Element root = doc.getDocumentElement();

        parseBeanDefinitions(root);
    }
    //遍历逐项进行注册
    protected void parseBeanDefinitions(Element root) {
        NodeList nl = root.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            Node node = nl.item(i);
            if (node instanceof Element) {
                Element ele = (Element) node;
                processBeanDefinition(ele);
            }
        }
    }
    //单个进行注册
    protected void processBeanDefinition(Element ele) {
        String name = ele.getAttribute("name");
        String className = ele.getAttribute("class");
        BeanDefinition beanDefinition = new BeanDefinition();
        processProperty(ele,beanDefinition);
        beanDefinition.setBeanClassName(className);
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
