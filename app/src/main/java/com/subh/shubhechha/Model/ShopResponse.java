package com.subh.shubhechha.Model;

import java.util.ArrayList;

public class ShopResponse {
    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public int status;
    public String message;
    public Data data;


    public class Data{
        public ArrayList<Shop> getShops() {
            return shops;
        }

        public void setShops(ArrayList<Shop> shops) {
            this.shops = shops;
        }

        public ArrayList<Shop> shops;
    }

    public class Shop{
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getLatitude() {
            return latitude;
        }

        public void setLatitude(String latitude) {
            this.latitude = latitude;
        }

        public String getLongitude() {
            return longitude;
        }

        public void setLongitude(String longitude) {
            this.longitude = longitude;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getOpen_time() {
            return open_time;
        }

        public void setOpen_time(String open_time) {
            this.open_time = open_time;
        }

        public String getClose_time() {
            return close_time;
        }

        public void setClose_time(String close_time) {
            this.close_time = close_time;
        }

        public int getModule_id() {
            return module_id;
        }

        public void setModule_id(int module_id) {
            this.module_id = module_id;
        }

        public double getDistance() {
            return distance;
        }

        public void setDistance(double distance) {
            this.distance = distance;
        }

        public int getDuration_minutes() {
            return duration_minutes;
        }

        public void setDuration_minutes(int duration_minutes) {
            this.duration_minutes = duration_minutes;
        }

        public String getDuration_formatted() {
            return duration_formatted;
        }

        public void setDuration_formatted(String duration_formatted) {
            this.duration_formatted = duration_formatted;
        }

        public int getActive_status() {
            return active_status;
        }

        public void setActive_status(int active_status) {
            this.active_status = active_status;
        }

        public String getImage_path() {
            return image_path;
        }

        public void setImage_path(String image_path) {
            this.image_path = image_path;
        }

        public int id;
        public String latitude;
        public String longitude;
        public String name;
        public String image;
        public int status;
        public String open_time;
        public String close_time;
        public int module_id;
        public double distance;
        public int duration_minutes;
        public String duration_formatted;
        public int active_status;
        public String image_path;
    }
}
