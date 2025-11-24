package com.subh.shubhechha.Model;

import java.util.ArrayList;

public class HomeResponse {
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


    public class Banner{
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getModule_service() {
            return module_service;
        }

        public void setModule_service(String module_service) {
            this.module_service = module_service;
        }

        public String getImage_path() {
            return image_path;
        }

        public void setImage_path(String image_path) {
            this.image_path = image_path;
        }

        public int id;
        public String image;
        public String module_service;
        public String image_path;
    }

    public class Data{
        public ArrayList<Banner> getBanners() {
            return banners;
        }

        public void setBanners(ArrayList<Banner> banners) {
            this.banners = banners;
        }

        public ArrayList<Module> getModules() {
            return modules;
        }

        public void setModules(ArrayList<Module> modules) {
            this.modules = modules;
        }

        public ArrayList<Banner> banners;
        public ArrayList<Module> modules;
    }

    public class Module{
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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

        public String getImage_path() {
            return image_path;
        }

        public void setImage_path(String image_path) {
            this.image_path = image_path;
        }

        public int id;
        public String name;
        public String image;
        public String image_path;
    }

}
