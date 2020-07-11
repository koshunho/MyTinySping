package com.huang.tinyioc.xml;

import com.huang.tinyioc.BeanDefinition;
import com.huang.tinyioc.BeanReference;
import com.huang.tinyioc.ProperiesList;
import com.huang.tinyioc.PropertyValue;
import com.huang.tinyioc.io.ResourceLoader;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.XPath;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

// 2020.7.12 测试dom4j读取XML
public class Dom4jBeanDefinitionReader extends AbstractBeanDefinitionReader{
    public Dom4jBeanDefinitionReader(ResourceLoader resourceLoader) {
        super(resourceLoader);
    }

    @Override
    public void loadBeanDefinitions(String location) throws Exception {
        InputStream is = getResourceLoader().getResource(location).getInputStream();

        SAXReader saxReader = new SAXReader();

        Document doc = saxReader.read(is);

        HashMap<String, String> hm = new HashMap<String, String>();

        hm.put("ns","http://www.springframework.org/schema/beans");

        XPath xPath = doc.createXPath("//ns:beans/ns:bean");

        xPath.setNamespaceURIs(hm);

        List<Element> beans = xPath.selectNodes(doc);

        for (Element bean : beans) {
            String name = bean.attributeValue("name");
            String clazz = bean.attributeValue("class");
            BeanDefinition beanDefinition = new BeanDefinition();

            ProperiesList properiesList = new ProperiesList();

            XPath propertyXpath = bean.createXPath("ns:property");
            propertyXpath.setNamespaceURIs(hm);

            List<Element> properties = propertyXpath.selectNodes(bean);
            for(Element property: properties) {
                String propertyName = property.attributeValue("name");
                String propertyValue = property.attributeValue("value");
                if (propertyValue != null && propertyValue.length() > 0) {
                    properiesList.addPropertyValue(new PropertyValue(propertyName, propertyValue));
                } else {
                    String ref = property.attributeValue("ref");
                    if (ref == null || ref.length() == 0) {
                        throw new IllegalArgumentException("Configuration problem: <property> element for property '"
                                + propertyName + "' must specify a ref or value");
                    }
                    BeanReference beanReference = new BeanReference(ref);
                    properiesList.addPropertyValue(new PropertyValue(propertyName, beanReference));
                }
            }
            beanDefinition.setProperiesList(properiesList);

            beanDefinition.setBeanClassName(clazz);
            getRegistry().put(name, beanDefinition);
        }
    }
}
