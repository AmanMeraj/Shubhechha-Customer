package com.subh.shubhechha.Model;

public class NotificationModel {
    private String title;
    private String subtitle;
    private String date;
    private int iconResId;

    public NotificationModel() {
    }

    public NotificationModel(String title, String subtitle, String date, int iconResId) {
        this.title = title;
        this.subtitle = subtitle;
        this.date = date;
        this.iconResId = iconResId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }
}