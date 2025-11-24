package com.subh.shubhechha.Model;

public class Category {
    private String name;
    private int iconResId; // For local drawable resources
    private String iconUrl; // For remote URL images
    private int backgroundColor;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    private int id;

    // Constructor for local drawable resources (dummy data)
    public Category(String name, int iconResId, int backgroundColor,int id) {
        this.name = name;
        this.iconResId = iconResId;
        this.iconUrl = null;
        this.backgroundColor = backgroundColor;
        this.id = id;
    }

    // Constructor for remote URLs (API data)
    public Category(String name, String iconUrl, int backgroundColor) {
        this.name = name;
        this.iconUrl = iconUrl;
        this.iconResId = 0;
        this.backgroundColor = backgroundColor;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    // Helper method to check if this is a local or remote image
    public boolean isRemoteImage() {
        return iconUrl != null && !iconUrl.isEmpty();
    }
}