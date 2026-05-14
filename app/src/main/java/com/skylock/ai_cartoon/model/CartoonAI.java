package com.skylock.ai_cartoon.model;

import android.graphics.Bitmap;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;
import com.skylock.ai_cartoon.util.EmptyUtils;

import java.util.ArrayList;
import java.util.List;

public class CartoonAI extends Representation implements Cloneable {

    @JsonProperty("enable")
    private boolean enable;
    private String gender;
    private List<ResultItem> results;
    private boolean selected;

    @SerializedName("show_home")
    private boolean showHome;

    @JsonProperty("name")
    private String name = "";
    private String styleName = "";

    @JsonProperty("icon")
    private String icon = "";
    private Bitmap bitmap = null;
    private String lottie = "";

    @JsonProperty("api")
    private List<String> api = new ArrayList();

    @JsonProperty("token")
    private String token = "";
    private String category = "Unknown";
    private boolean premium = false;

    public String getGender() {
        return this.gender;
    }

    public void setGender(String str) {
        this.gender = str;
    }

    public CartoonAI() {
        this.enable = false;
        this.showHome = false;
        this.selected = false;
        this.results = new ArrayList();
        this.enable = true;
        this.showHome = true;
        this.selected = false;
        this.results = new ArrayList();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String str) {
        this.name = str;
    }

    public String getIcon() {
        return this.icon;
    }

    public void setIcon(String str) {
        this.icon = str;
    }

    public List<String> getApi() {
        return this.api;
    }

    public void setApi(List<String> list) {
        this.api = list;
    }

    public Bitmap getBitmap() {
        return this.bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isSelected() {
        return this.selected;
    }

    public void setSelected(boolean z) {
        this.selected = z;
    }

    public String getToken() {
        return "Bearer NU4IYAS4D0F8CVBSI26R5NU21E0HW737GPJ07WAM";
    }

    public void setToken(String str) {
        this.token = str;
    }

    public boolean isEnable() {
        return this.enable;
    }

    public void setEnable(boolean z) {
        this.enable = z;
    }

    public String getStyleName() {
        return this.styleName;
    }

    public void setStyleName(String str) {
        this.styleName = str;
    }

    public boolean isPremium() {
        return this.premium;
    }

    public void setPremium(boolean z) {
        this.premium = z;
    }

    public String getCategory() {
        return this.category;
    }

    public void setCategory(String str) {
        this.category = str;
    }

    public List<ResultItem> getResults() {
        if (EmptyUtils.isEmpty(this.results)) {
            this.results = new ArrayList();
        }
        return this.results;
    }

    public void setResults(List<ResultItem> list) {
        this.results = list;
    }

    public String getLottie() {
        return this.lottie;
    }

    public void setLottie(String str) {
        this.lottie = str;
    }

    public boolean isShowHome() {
        return this.showHome;
    }

    public void setShowHome(boolean z) {
        this.showHome = z;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    /* loaded from: classes2.dex */
    public class Thumb {
        private String after;
        private Bitmap afterBitmap;
        private String before;
        private Bitmap beforeBitmap;

        public Thumb(CartoonAI cartoonAI) {
        }

        public String getAfter() {
            return this.after;
        }

        public void setAfter(String str) {
            this.after = str;
        }

        public String getBefore() {
            return this.before;
        }

        public void setBefore(String str) {
            this.before = str;
        }

        public Bitmap getAfterBitmap() {
            return this.afterBitmap;
        }

        public void setAfterBitmap(Bitmap bitmap) {
            this.afterBitmap = bitmap;
        }

        public Bitmap getBeforeBitmap() {
            return this.beforeBitmap;
        }

        public void setBeforeBitmap(Bitmap bitmap) {
            this.beforeBitmap = bitmap;
        }
    }
}
