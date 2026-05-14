package com.skylock.ai_cartoon.model;

public class EnhanceApi extends Representation {
    private String api;
    private Integer process;
    private String token;
    private String type;
    private String typeAiFaceAnimation = "liveportrait";

    public String getTypeAiFaceAnimation() {
        return this.typeAiFaceAnimation;
    }

    public void setTypeAiFaceAnimation(String str) {
        this.typeAiFaceAnimation = str;
    }

    public String getType() {
        return this.type;
    }

    public void setType(String str) {
        this.type = str;
    }

    public String getApi() {
        return this.api;
    }

    public void setApi(String str) {
        this.api = str;
    }

    public String getToken() {
        return this.token;
    }

    public void setToken(String str) {
        this.token = str;
    }

    public Integer getProcess() {
        return this.process;
    }

    public void setProcess(Integer num) {
        this.process = num;
    }
}

