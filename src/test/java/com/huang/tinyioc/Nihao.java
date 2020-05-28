package com.huang.tinyioc;

import org.junit.Assert;

public class Nihao {
    private Konnichiha konnichiha;

    public void sayNihao(String text){
        Assert.assertNotNull(konnichiha);
        System.out.println(text);
    }

    public void setKonnichiha(Konnichiha konnichiha) {
        this.konnichiha = konnichiha;
    }
}
