package com.subh.shubhechha.Model;

public class AddressModel {
    private int id;
    private String address;
    private String tag;

    public AddressModel(String address, String tag) {
        this.address = address;
        this.tag = tag;
    }

    public AddressModel(int id, String address, String tag) {
        this.id = id;
        this.address = address;
        this.tag = tag;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
}