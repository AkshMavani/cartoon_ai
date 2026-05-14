package com.skylock.ai_cartoon.model;

public class RunpodResponse extends Representation {
    private String id;
    private AiphotoResponse output;
    private String status;

    public String getId() {
        return this.id;
    }

    public void setId(String str) {
        this.id = str;
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(String str) {
        this.status = str;
    }

    public AiphotoResponse getOutput() {
        return this.output;
    }

    public void setOutput(AiphotoResponse aiphotoResponse) {
        this.output = aiphotoResponse;
    }
}
