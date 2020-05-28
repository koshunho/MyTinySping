package com.huang.tinyioc.io;

import java.net.URL;

public class ResourceLoader {
    //反射获取URLResource实例对象
    public Resource getResource(String location){
        URL url = this.getClass().getClassLoader().getResource(location);
        return new UrlResource(url);
    }
}

