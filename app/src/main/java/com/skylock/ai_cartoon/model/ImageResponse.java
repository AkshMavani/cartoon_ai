package com.skylock.ai_cartoon.model;

import java.io.Serializable;

public class ImageResponse extends Representation implements Serializable {
    private String id;
    private boolean loaded = false;
    private String name;
    private SizeImage size;
    private String tempName;
    private String url;
    private String urlBefore;

    public ImageResponse() {
    }

    public ImageResponse(String str, String str2, String str3, SizeImage sizeImage) {
        this.id = str;
        this.name = str2;
        this.url = str3;
        this.size = sizeImage;
    }

    public ImageResponse(String str, String str2, String str3, SizeImage sizeImage, String str4) {
        this.id = str;
        this.name = str2;
        this.url = str3;
        this.size = sizeImage;
        this.tempName = str4;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String str) {
        this.url = str;
    }

    public SizeImage getSize() {
        return this.size;
    }

    public void setSize(SizeImage sizeImage) {
        this.size = sizeImage;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getTempName() {
        return this.tempName;
    }

    public void setTempName(String str) {
        this.tempName = str;
    }

    public String getUrlBefore() {
        return this.urlBefore;
    }

    public void setUrlBefore(String str) {
        this.urlBefore = str;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void setLoaded(boolean z) {
        this.loaded = z;
    }
}

