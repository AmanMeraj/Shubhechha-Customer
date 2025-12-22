package com.subh.shubhechha.Model;

import java.util.ArrayList;

public class CartResponse {

    private int status;
    private String message;
    private Data data;

    // ---------------------- GETTERS & SETTERS ----------------------

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

    // ---------------------- INNER CLASSES ----------------------

    public static class Cart {
        private int id;
        private int user_id;
        private int promo_id;
        private int shop_id;
        private String created_at;
        private String updated_at;
        private ArrayList<CartItem> cart_items;

        // Getters & Setters
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

        public int getPromo_id() {
            return promo_id;
        }

        public void setPromo_id(int promo_id) {
            this.promo_id = promo_id;
        }

        public int getShop_id() {
            return shop_id;
        }

        public void setShop_id(int shop_id) {
            this.shop_id = shop_id;
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

        public ArrayList<CartItem> getCart_items() {
            return cart_items;
        }

        public void setCart_items(ArrayList<CartItem> cart_items) {
            this.cart_items = cart_items;
        }
    }

    public static class CartItem {
        private int id;
        private int cart_id;
        private int item_id;
        private int shop_id;
        private int vendor_id;
        private String quantity;
        private String created_at;
        private String updated_at;
        private Item item;
        private ArrayList<Integer> added_modifiers;

        // Getters & Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getCart_id() {
            return cart_id;
        }

        public void setCart_id(int cart_id) {
            this.cart_id = cart_id;
        }

        public int getItem_id() {
            return item_id;
        }

        public void setItem_id(int item_id) {
            this.item_id = item_id;
        }

        public int getShop_id() {
            return shop_id;
        }

        public void setShop_id(int shop_id) {
            this.shop_id = shop_id;
        }

        public int getVendor_id() {
            return vendor_id;
        }

        public void setVendor_id(int vendor_id) {
            this.vendor_id = vendor_id;
        }

        public String getQuantity() {
            return quantity;
        }

        public void setQuantity(String quantity) {
            this.quantity = quantity;
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

        public Item getItem() {
            return item;
        }

        public void setItem(Item item) {
            this.item = item;
        }

        public ArrayList<Integer> getAdded_modifiers() {
            return added_modifiers;
        }

        public void setAdded_modifiers(ArrayList<Integer> added_modifiers) {
            this.added_modifiers = added_modifiers;
        }
    }

    public static class Data {
        private Cart cart;
        private int sub_total;
        private int total;
        private int delivery_charge;
        private int discount_amount;
        private int cart_item_count;
        private int packaging_charge;
        private int gst_on_item_total;
        private int gst_on_packaging_charge;
        private int gst_on_delivery_charge;
        private int total_tax;
        private Object coupon;

        // Getters & Setters
        public Cart getCart() {
            return cart;
        }

        public void setCart(Cart cart) {
            this.cart = cart;
        }

        public int getSub_total() {
            return sub_total;
        }

        public void setSub_total(int sub_total) {
            this.sub_total = sub_total;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getDelivery_charge() {
            return delivery_charge;
        }

        public void setDelivery_charge(int delivery_charge) {
            this.delivery_charge = delivery_charge;
        }

        public int getDiscount_amount() {
            return discount_amount;
        }

        public void setDiscount_amount(int discount_amount) {
            this.discount_amount = discount_amount;
        }

        public int getCart_item_count() {
            return cart_item_count;
        }

        public void setCart_item_count(int cart_item_count) {
            this.cart_item_count = cart_item_count;
        }

        public int getPackaging_charge() {
            return packaging_charge;
        }

        public void setPackaging_charge(int packaging_charge) {
            this.packaging_charge = packaging_charge;
        }

        public int getGst_on_item_total() {
            return gst_on_item_total;
        }

        public void setGst_on_item_total(int gst_on_item_total) {
            this.gst_on_item_total = gst_on_item_total;
        }

        public int getGst_on_packaging_charge() {
            return gst_on_packaging_charge;
        }

        public void setGst_on_packaging_charge(int gst_on_packaging_charge) {
            this.gst_on_packaging_charge = gst_on_packaging_charge;
        }

        public int getGst_on_delivery_charge() {
            return gst_on_delivery_charge;
        }

        public void setGst_on_delivery_charge(int gst_on_delivery_charge) {
            this.gst_on_delivery_charge = gst_on_delivery_charge;
        }

        public int getTotal_tax() {
            return total_tax;
        }

        public void setTotal_tax(int total_tax) {
            this.total_tax = total_tax;
        }

        public Object getCoupon() {
            return coupon;
        }

        public void setCoupon(Object coupon) {
            this.coupon = coupon;
        }
    }

    public static class Item {
        private int id;
        private int shop_id;
        private String image;
        private String name;
        private String amount;
        private String offer_price;
        private String description;
        private int veg;
        private int status;
        private int user_item_rating;
        private String average_rating;
        private String image_path;

        // Getters & Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getShop_id() {
            return shop_id;
        }

        public void setShop_id(int shop_id) {
            this.shop_id = shop_id;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public String getOffer_price() {
            return offer_price;
        }

        public void setOffer_price(String offer_price) {
            this.offer_price = offer_price;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public int getVeg() {
            return veg;
        }

        public void setVeg(int veg) {
            this.veg = veg;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getUser_item_rating() {
            return user_item_rating;
        }

        public void setUser_item_rating(int user_item_rating) {
            this.user_item_rating = user_item_rating;
        }

        public String getAverage_rating() {
            return average_rating;
        }

        public void setAverage_rating(String average_rating) {
            this.average_rating = average_rating;
        }

        public String getImage_path() {
            return image_path;
        }

        public void setImage_path(String image_path) {
            this.image_path = image_path;
        }
    }
}
