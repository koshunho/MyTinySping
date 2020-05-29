package com.huang.tinyioc;

import org.junit.Assert;

public class Nihao {
    private KonnichihaImpl konnichihaImpl;

    public void sayNihao(String text){
        Assert.assertNotNull(konnichihaImpl);
        System.out.println(text);
    }

    public void setKonnichihaImpl(KonnichihaImpl konnichihaImpl) {
        this.konnichihaImpl = konnichihaImpl;
    }
}
