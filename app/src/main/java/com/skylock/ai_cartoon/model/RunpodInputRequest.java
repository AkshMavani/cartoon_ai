package com.skylock.ai_cartoon.model;

/* loaded from: classes9.dex */
public class RunpodInputRequest {
    private String image_base64;
    private String image_url;
    private String prompt;
    private Integer scale;
    private double strength;
    private String style;

    public String getPrompt() {
        return this.prompt;
    }

    public void setPrompt(String str) {
        this.prompt = str;
    }

    public String getStyle() {
        return this.style;
    }

    public void setStyle(String str) {
        this.style = str;
    }

    public String getImageBase64() {
        return this.image_base64;
    }

    public void setImageBase64(String str) {
        this.image_base64 = str;
    }

    public String getImageUrl() {
        return this.image_url;
    }

    public void setImageUrl(String str) {
        this.image_url = str;
    }

    public double getStrength() {
        return this.strength;
    }

    public void setStrength(double d) {
        this.strength = d;
    }

    public Integer getScale() {
        return this.scale;
    }

    public void setScale(Integer num) {
        this.scale = num;
    }
}
