package com.huang.tinyioc;

public class Konnichiha {
    private String text;

    public void say(){
        System.out.println(text);
    }

    public void setText(String text) {
        this.text = text;
    }
}
