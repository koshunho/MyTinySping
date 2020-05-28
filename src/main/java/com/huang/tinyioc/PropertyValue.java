package com.huang.tinyioc;


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
