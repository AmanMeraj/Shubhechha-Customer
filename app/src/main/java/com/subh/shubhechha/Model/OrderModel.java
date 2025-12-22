package com.subh.shubhechha.Model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class OrderModel {

    @SerializedName("status")
    private int status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private Data data;

    // Getters and Setters
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

    // Data Class
    public static class Data {
        @SerializedName("orders")
        private Orders orders;

        public Orders getOrders() {
            return orders;
        }

        public void setOrders(Orders orders) {
            this.orders = orders;
        }
    }

    // Orders Class (Pagination wrapper)
    public static class Orders {
        @SerializedName("current_page")
        private int current_page;

        @SerializedName("data")
        private List<Order> data;

        @SerializedName("first_page_url")
        private String first_page_url;

        @SerializedName("from")
        private Integer from;

        @SerializedName("last_page")
        private int last_page;

        @SerializedName("last_page_url")
        private String last_page_url;

        @SerializedName("links")
        private List<PageLink> links;

        @SerializedName("next_page_url")
        private String next_page_url;

        @SerializedName("path")
        private String path;

        @SerializedName("per_page")
        private int per_page;

        @SerializedName("prev_page_url")
        private String prev_page_url;

        @SerializedName("to")
        private Integer to;

        @SerializedName("total")
        private int total;

        // Getters and Setters
        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }

        public List<Order> getData() {
            return data;
        }

        public void setData(List<Order> data) {
            this.data = data;
        }

        public String getFirst_page_url() {
            return first_page_url;
        }

        public void setFirst_page_url(String first_page_url) {
            this.first_page_url = first_page_url;
        }

        public Integer getFrom() {
            return from;
        }

        public void setFrom(Integer from) {
            this.from = from;
        }

        public int getLast_page() {
            return last_page;
        }

        public void setLast_page(int last_page) {
            this.last_page = last_page;
        }

        public String getLast_page_url() {
            return last_page_url;
        }

        public void setLast_page_url(String last_page_url) {
            this.last_page_url = last_page_url;
        }

        public List<PageLink> getLinks() {
            return links;
        }

        public void setLinks(List<PageLink> links) {
            this.links = links;
        }

        public String getNext_page_url() {
            return next_page_url;
        }

        public void setNext_page_url(String next_page_url) {
            this.next_page_url = next_page_url;
        }

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }

        public int getPer_page() {
            return per_page;
        }

        public void setPer_page(int per_page) {
            this.per_page = per_page;
        }

        public String getPrev_page_url() {
            return prev_page_url;
        }

        public void setPrev_page_url(String prev_page_url) {
            this.prev_page_url = prev_page_url;
        }

        public Integer getTo() {
            return to;
        }

        public void setTo(Integer to) {
            this.to = to;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    // Order Class
    public static class Order {
        @SerializedName("id")
        private int id;

        @SerializedName("orderno")
        private String orderno;

        @SerializedName("created_at")
        private String created_at;

        @SerializedName("status")
        private String status;

        @SerializedName("total")
        private String total;

        @SerializedName("shop_id")
        private int shop_id;

        @SerializedName("shop_name")
        private String shop_name;

        @SerializedName("address_id")
        private int address_id;

        @SerializedName("address")
        private String address;

        @SerializedName("pincode")
        private String pincode;

        @SerializedName("receipent_name")
        private String receipent_name;

        @SerializedName("user_mobile")
        private String user_mobile;

        @SerializedName("user_image")
        private String user_image;

        @SerializedName("shop_image")
        private String shop_image;

        // Getters and Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getOrderno() {
            return orderno;
        }

        public void setOrderno(String orderno) {
            this.orderno = orderno;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public int getShop_id() {
            return shop_id;
        }

        public void setShop_id(int shop_id) {
            this.shop_id = shop_id;
        }

        public String getShop_name() {
            return shop_name;
        }

        public void setShop_name(String shop_name) {
            this.shop_name = shop_name;
        }

        public int getAddress_id() {
            return address_id;
        }

        public void setAddress_id(int address_id) {
            this.address_id = address_id;
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

        public String getReceipent_name() {
            return receipent_name;
        }

        public void setReceipent_name(String receipent_name) {
            this.receipent_name = receipent_name;
        }

        public String getUser_mobile() {
            return user_mobile;
        }

        public void setUser_mobile(String user_mobile) {
            this.user_mobile = user_mobile;
        }

        public String getUser_image() {
            return user_image;
        }

        public void setUser_image(String user_image) {
            this.user_image = user_image;
        }

        public String getShop_image() {
            return shop_image;
        }

        public void setShop_image(String shop_image) {
            this.shop_image = shop_image;
        }
    }

    // PageLink Class (for pagination links)
    public static class PageLink {
        @SerializedName("url")
        private String url;

        @SerializedName("label")
        private String label;

        @SerializedName("active")
        private boolean active;

        // Getters and Setters
        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getLabel() {
            return label;
        }

        public void setLabel(String label) {
            this.label = label;
        }

        public boolean isActive() {
            return active;
        }

        public void setActive(boolean active) {
            this.active = active;
        }
    }
}