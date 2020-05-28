package com.huang.tinyioc.xml;

import java.io.IOException;

public interface BeanDefinationReader {
    void loadBeanDefinitions(String location) throws Exception;
}
