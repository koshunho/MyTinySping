package com.huang.tinyioc;

import java.util.ArrayList;
import java.util.List;

public class ProperiesList {
    private final List<PropertyValue> list = new ArrayList<PropertyValue>();

    public ProperiesList(){

    }

    public void addPropertyValue(PropertyValue pv){
        list.add(pv);
    }

    public List<PropertyValue> getList() {
        return list;
    }
}
