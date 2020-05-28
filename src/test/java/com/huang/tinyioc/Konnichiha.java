package com.huang.tinyioc;

public class Konnichiha {
    private String text;

    private Nihao nihao;

    public void say(){
        nihao.sayNihao(text);
    }

    public void setText(String text) {
        this.text = text;
    }

    public void setNihao(Nihao nihao) {
        this.nihao = nihao;
    }
}
