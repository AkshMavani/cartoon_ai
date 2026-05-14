package com.skylock.ai_cartoon.model;

import java.io.Serializable;

public class SizeImage extends Representation implements Serializable {
    private Integer height;
    private Integer width;

    public SizeImage(Integer num, Integer num2) {
        this.height = num2;
        this.width = num;
    }

    public Integer getHeight() {
        return this.height;
    }

    public void setHeight(Integer num) {
        this.height = num;
    }

    public Integer getWidth() {
        return this.width;
    }

    public void setWidth(Integer num) {
        this.width = num;
    }
}