package com.subh.shubhechha.Model;


public class Shop {
    private String name;
    private String time;
    private String distance;
    private int imageResId;

    // Constructor
    public Shop(String name, String time, String distance, int imageResId) {
        this.name = name;
        this.time = time;
        this.distance = distance;
        this.imageResId = imageResId;
    }

    // Getters
    public String getName() {
        return name;
    }

    public String getTime() {
        return time;
    }

    public String getDistance() {
        return distance;
    }

    public int getImageResId() {
        return imageResId;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }
}