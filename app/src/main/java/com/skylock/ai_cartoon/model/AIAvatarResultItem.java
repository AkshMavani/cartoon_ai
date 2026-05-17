package com.skylock.ai_cartoon.model;


/**
 * Model representing a single generated AI Avatar result item.
 * Used by ResultItemAdapter to display multiple generated results for one style.
 */
public class AIAvatarResultItem {

    private String title;       // e.g. "Result 1", "Result 2"
    private String urlAfter;    // The generated cartoon/avatar image URL
    private String beautifierUrl; // Optional: URL of the beautified (enhanced) version
    private boolean selected;   // Whether this result is currently selected/displayed
    private boolean beautifierSelected; // Whether the beautifier version is being shown

    public AIAvatarResultItem() {
    }

    public AIAvatarResultItem(String title, String urlAfter, boolean selected) {
        this.title = title;
        this.urlAfter = urlAfter;
        this.selected = selected;
        this.beautifierSelected = false;
        this.beautifierUrl = null;
    }

    // ── Getters & Setters ──────────────────────────────────────────────────

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrlAfter() {
        return urlAfter;
    }

    public void setUrlAfter(String urlAfter) {
        this.urlAfter = urlAfter;
    }

    public String getBeautifierUrl() {
        return beautifierUrl;
    }

    public void setBeautifierUrl(String beautifierUrl) {
        this.beautifierUrl = beautifierUrl;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public boolean isBeautifierSelected() {
        return beautifierSelected;
    }

    public void setBeautifierSelected(boolean beautifierSelected) {
        this.beautifierSelected = beautifierSelected;
    }

    /**
     * Returns the currently active display URL:
     * - Beautifier URL if beautifier mode is active and beautifier URL exists
     * - Otherwise the original generated URL
     */
    public String getActiveUrl() {
        if (beautifierSelected && beautifierUrl != null && !beautifierUrl.isEmpty()) {
            return beautifierUrl;
        }
        return urlAfter;
    }
}