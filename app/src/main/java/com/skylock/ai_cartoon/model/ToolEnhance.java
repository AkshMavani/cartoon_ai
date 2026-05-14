package com.skylock.ai_cartoon.model;


import java.util.Objects;

/**
 * Java equivalent of the ToolEnhance Kotlin data class.
 */
public final class ToolEnhance {
    private String name;
    private int icon;
    private String feature;

    // Constructor
    public ToolEnhance(String name, int icon, String feature) {
        if (name == null) throw new NullPointerException("name cannot be null");
        if (feature == null) throw new NullPointerException("feature cannot be null");

        this.name = name;
        this.icon = icon;
        this.feature = feature;
    }

    // Getters
    public String getName() {
        return name;
    }

    public int getIcon() {
        return icon;
    }

    public String getFeature() {
        return feature;
    }

    // Setters
    public void setName(String name) {
        if (name == null) throw new NullPointerException("name cannot be null");
        this.name = name;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public void setFeature(String feature) {
        if (feature == null) throw new NullPointerException("feature cannot be null");
        this.feature = feature;
    }

    // Equivalent to Kotlin's component1()
    public String component1() {
        return this.name;
    }

    // Equivalent to Kotlin's component2()
    public int component2() {
        return this.icon;
    }

    // Equivalent to Kotlin's component3()
    public String component3() {
        return this.feature;
    }

    // Equivalent to Kotlin's copy() method
    public ToolEnhance copy(String name, int icon, String feature) {
        return new ToolEnhance(name, icon, feature);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ToolEnhance that = (ToolEnhance) o;
        return icon == that.icon &&
                Objects.equals(name, that.name) &&
                Objects.equals(feature, that.feature);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, icon, feature);
    }

    @Override
    public String toString() {
        return "ToolEnhance(name=" + name + ", icon=" + icon + ", feature=" + feature + ")";
    }
}