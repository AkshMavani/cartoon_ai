package com.skylock.ai_cartoon.model;

import java.io.Serializable;

public class ImageFace implements Serializable {
    private Boolean afterLoaded;
    private Boolean beforeLoaded;
    private Boolean selected;
    private SizeImage size;
    private String urlAfter;
    private String urlBefore;
    private String urlSave;

    public ImageFace() {
        this.beforeLoaded = false;
        this.afterLoaded = false;
        this.selected = false;
        this.size = new SizeImage(512, 512);
    }

    public ImageFace(String str, String str2, SizeImage sizeImage) {
        this.beforeLoaded = false;
        this.afterLoaded = false;
        this.selected = false;
        new SizeImage(512, 512);
        this.urlBefore = str;
        this.urlAfter = str2;
        this.size = sizeImage;
    }

    public String getUrlBefore() {
        return this.urlBefore;
    }

    public void setUrlBefore(String str) {
        this.urlBefore = str;
    }

    public String getUrlAfter() {
        return this.urlAfter;
    }

    public void setUrlAfter(String str) {
        this.urlAfter = str;
    }

    public void setBeforeLoaded(Boolean bool) {
        this.beforeLoaded = bool;
    }

    public void setAfterLoaded(Boolean bool) {
        this.afterLoaded = bool;
    }

    public Boolean getBeforeLoaded() {
        return this.beforeLoaded;
    }

    public Boolean getAfterLoaded() {
        return this.afterLoaded;
    }

    public SizeImage getSize() {
        return this.size;
    }

    public void setSize(SizeImage sizeImage) {
        this.size = sizeImage;
    }

    public Boolean getSelected() {
        return this.selected;
    }

    public void setSelected(Boolean bool) {
        this.selected = bool;
    }

    public String getUrlSave() {
        return this.urlSave;
    }

    public void setUrlSave(String str) {
        this.urlSave = str;
    }
}
