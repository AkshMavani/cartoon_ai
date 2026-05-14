package com.skylock.ai_cartoon.model;

import android.graphics.Bitmap;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.Serializable;

public class PhotoLibrary extends Representation implements Serializable {
    private String category;
    private int icon;
    private boolean isCamera;
    private String name;
    private String path;
    private Bitmap thumb;
    private String thumbUri;
    private String uri;
    private int selectNumber = 0;
    private int width = 0;
    private int height = 0;

    public PhotoLibrary(String str, String str2, String str3, Bitmap bitmap, String str4) {
        this.name = str;
        this.path = str2;
        this.uri = str3;
        this.thumb = bitmap;
        this.category = str4;
    }

    public PhotoLibrary(String str) {
        this.uri = str;
    }

    public PhotoLibrary(int i) {
        this.icon = i;
    }

    public PhotoLibrary(boolean z) {
        this.isCamera = z;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getPath() {
        return this.path;
    }

    public void setPath(String str) {
        this.path = str;
    }

    public String getUri() {
        return this.uri;
    }

    public void setUri(String str) {
        this.uri = str;
    }

    public Bitmap getThumb() {
        return this.thumb;
    }

    public void setThumb(Bitmap bitmap) {
        this.thumb = bitmap;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String str) {
        this.category = str;
    }

    public String getThumbUri() {
        return this.thumbUri;
    }

    public void setThumbUri(String str) {
        this.thumbUri = str;
    }

    public int getSelectNumber() {
        return this.selectNumber;
    }

    public void setSelectNumber(int i) {
        this.selectNumber = i;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int i) {
        this.width = i;
    }

    public int getHeight() {
        return this.height;
    }

    public void setHeight(int i) {
        this.height = i;
    }

    public void setCamera(boolean z) {
        this.isCamera = z;
    }

    public boolean isCamera() {
        return this.isCamera;
    }

    public int getIcon() {
        return this.icon;
    }
}

