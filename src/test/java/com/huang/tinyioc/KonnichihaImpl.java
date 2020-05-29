package com.huang.tinyioc;

public class KonnichihaImpl implements Konnichiha{
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
