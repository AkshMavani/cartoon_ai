package com.skylock.ai_cartoon.model;

public class HealthResponse extends Representation {
    private Integer code;
    private String msg;
    private Integer process;

    public Integer getCode() {
        return this.code;
    }

    public void setCode(Integer num) {
        this.code = num;
    }

    public String getMsg() {
        return this.msg;
    }

    public void setMsg(String str) {
        this.msg = str;
    }

    public Integer getProcess() {
        return this.process;
    }

    public void setProcess(Integer num) {
        this.process = num;
    }
}
