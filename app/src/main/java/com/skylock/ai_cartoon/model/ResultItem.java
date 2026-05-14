package com.skylock.ai_cartoon.model;

import java.io.Serializable;

public class ResultItem extends Representation implements Cloneable, Serializable {
    private Boolean beautifierSelected;
    private String beautifierUrl;
    private String name;
    private Boolean selected;
    private SizeImage size;
    private String urlAfter;
    private String urlBefore;
    private String urlSave;

    public ResultItem(String str, String str2, Boolean bool) {
        this.selected = false;
        this.beautifierSelected = false;
        this.size = new SizeImage(512, 512);
        this.name = str;
        this.urlAfter = str2;
        this.selected = bool;
    }

    public ResultItem(String str, String str2, String str3, SizeImage sizeImage, Boolean bool) {
        this.selected = false;
        this.beautifierSelected = false;
        new SizeImage(512, 512);
        this.name = str;
        this.urlAfter = str3;
        this.urlBefore = str2;
        this.selected = bool;
        this.size = sizeImage;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getUrlAfter() {
        return this.urlAfter;
    }

    public void setUrlAfter(String str) {
        this.urlAfter = str;
    }

    public String getBeautifierUrl() {
        return this.beautifierUrl;
    }

    public void setBeautifierUrl(String str) {
        this.beautifierUrl = str;
    }

    public Boolean getSelected() {
        return this.selected;
    }

    public void setSelected(Boolean bool) {
        this.selected = bool;
    }

    public Boolean getBeautifierSelected() {
        return this.beautifierSelected;
    }

    public void setBeautifierSelected(Boolean bool) {
        this.beautifierSelected = bool;
    }

    public String getUrlBefore() {
        return this.urlBefore;
    }

    public void setUrlBefore(String str) {
        this.urlBefore = str;
    }

    public String getUrlSave() {
        return this.urlSave;
    }

    public void setUrlSave(String str) {
        this.urlSave = str;
    }

    public SizeImage getSize() {
        return this.size;
    }

    public void setSize(SizeImage sizeImage) {
        this.size = sizeImage;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override // mobi.zeezoo.photoenhancer.model.Representation
    public String toString() {
        return "ResultItem{name='" + this.name + "', urlAfter='" + this.urlAfter + "', urlBefore='" + this.urlBefore + "', selected=" + this.selected + ", beautifierUrl='" + this.beautifierUrl + "', beautifierSelected=" + this.beautifierSelected + ", urlSave='" + this.urlSave + "', size=" + this.size + '}';
    }
}
