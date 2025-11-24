package com.subh.shubhechha.Model;

import com.google.gson.annotations.SerializedName;

public class User {

    @SerializedName("mobile")
    private String phone;

    @SerializedName("otp")
    private String otp;

    @SerializedName("type")
    private String type;

    @SerializedName("name")
    private String name;

    @SerializedName("email")
    private String email;

    @SerializedName("address")
    private String address;

    // Getters and Setters

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return "User{" +
                "phone='" + phone + '\'' +
                ", otp='" + otp + '\'' +
                ", type='" + type + '\'' +
                ", name='" + name + '\'' +
                ", email='" + email + '\'' +
                ", address='" + address + '\'' +
                '}';
    }
}