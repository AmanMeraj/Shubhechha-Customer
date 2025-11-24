package com.subh.shubhechha.Model;

import java.util.ArrayList;

public class GetAddressResponse {
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

    public class Address{
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
        }

        public String getComplex_id() {
            return complex_id;
        }

        public void setComplex_id(String complex_id) {
            this.complex_id = complex_id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public String getPincode() {
            return pincode;
        }

        public void setPincode(String pincode) {
            this.pincode = pincode;
        }

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }

        public String getBuilding() {
            return building;
        }

        public void setBuilding(String building) {
            this.building = building;
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

        public String getFloor() {
            return floor;
        }

        public void setFloor(String floor) {
            this.floor = floor;
        }

        public String getFlat_number() {
            return flat_number;
        }

        public void setFlat_number(String flat_number) {
            this.flat_number = flat_number;
        }

        public String getReceipent_name() {
            return receipent_name;
        }

        public void setReceipent_name(String receipent_name) {
            this.receipent_name = receipent_name;
        }

        public String getPhone() {
            return phone;
        }

        public void setPhone(String phone) {
            this.phone = phone;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getUpdated_at() {
            return updated_at;
        }

        public void setUpdated_at(String updated_at) {
            this.updated_at = updated_at;
        }

        public String getDeleted_at() {
            return deleted_at;
        }

        public void setDeleted_at(String deleted_at) {
            this.deleted_at = deleted_at;
        }

        public int getIs_delivery() {
            return is_delivery;
        }

        public void setIs_delivery(int is_delivery) {
            this.is_delivery = is_delivery;
        }

        public int id;
        public int user_id;
        public String complex_id;
        public String name;
        public String address;
        public String pincode;
        public String state;
        public String country;
        public String building;
        public String latitude;
        public String longitude;
        public String floor;
        public String flat_number;
        public String receipent_name;
        public String phone;
        public String created_at;
        public String updated_at;
        public String deleted_at;
        public int is_delivery;
    }

    public class Data{
        public int getWallet_amount() {
            return wallet_amount;
        }

        public void setWallet_amount(int wallet_amount) {
            this.wallet_amount = wallet_amount;
        }

        public ArrayList<PaymentMethod> getPayment_methods() {
            return payment_methods;
        }

        public void setPayment_methods(ArrayList<PaymentMethod> payment_methods) {
            this.payment_methods = payment_methods;
        }

        public ArrayList<Address> getAddresses() {
            return addresses;
        }

        public void setAddresses(ArrayList<Address> addresses) {
            this.addresses = addresses;
        }

        public int wallet_amount;
        public ArrayList<PaymentMethod> payment_methods;
        public ArrayList<Address> addresses;
    }

    public class PaymentMethod{
        public String getDisplay_name() {
            return display_name;
        }

        public void setDisplay_name(String display_name) {
            this.display_name = display_name;
        }

        public String getSlug() {
            return slug;
        }

        public void setSlug(String slug) {
            this.slug = slug;
        }

        public String display_name;
        public String slug;
    }
}
