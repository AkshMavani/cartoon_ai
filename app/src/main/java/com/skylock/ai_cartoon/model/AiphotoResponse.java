package com.skylock.ai_cartoon.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AiphotoResponse extends Representation implements Serializable {
    private Integer code;
    public int countPreview;
    private String data;
    private int index;
    private String msg;
    private SizeImage size;
    public String video_name;
    public String video_url;
    private List<ImageResponse> images = new ArrayList();
    private List<ImageFace> faces = new ArrayList();

    public String getData() {
        return this.data;
    }

    public void setData(String str) {
        this.data = str;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String str) {
        this.msg = str;
    }

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer num) {
        this.code = num;
    }

    public SizeImage getSize() {
        return this.size;
    }

    public void setSize(SizeImage sizeImage) {
        this.size = sizeImage;
    }

    public List<ImageResponse> getImages() {
        return this.images;
    }

    public void setImages(List<ImageResponse> list) {
        this.images = list;
    }

    public List<ImageFace> getFaces() {
        return this.faces;
    }

    public void setFaces(List<ImageFace> list) {
        this.faces = list;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int i) {
        this.index = i;
    }
}
