package com.subh.shubhechha.Model;

public class BannerItem {
    private int id;
    private int imageRes; // For local drawable resources
    private String imageUrl; // For remote URL images
    private String title;

    // Constructor for local drawable resources (dummy data)
    public BannerItem(int id, int imageRes) {
        this.id = id;
        this.imageRes = imageRes;
        this.imageUrl = null;
        this.title = "";
    }

    // Constructor for remote URLs (API data)
    public BannerItem(int id, String imageUrl, String title) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.title = title;
        this.imageRes = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getImageRes() {
        return imageRes;
    }

    public void setImageRes(int imageRes) {
        this.imageRes = imageRes;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    // Helper method to check if this is a local or remote image
    public boolean isRemoteImage() {
        return imageUrl != null && !imageUrl.isEmpty();
    }
}