package com.subh.shubhechha.Model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;

public class ShopItemResponse {
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

    public static class Data {
        public ArrayList<Menu> getMenus() {
            return menus;
        }

        public void setMenus(ArrayList<Menu> menus) {
            this.menus = menus;
        }

        public Items getItems() {
            return items;
        }

        public void setItems(Items items) {
            this.items = items;
        }

        public ArrayList<Menu> menus;
        public Items items;
    }

    public static class Menu {
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getModule_listing_id() {
            return module_listing_id;
        }

        public void setModule_listing_id(int module_listing_id) {
            this.module_listing_id = module_listing_id;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
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
        public int module_listing_id;
        public int status;
        public String name;
        public String image;
        public String image_path;
    }

    public static class Items {
        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }

        public ArrayList<Datum> getData() {
            return data;
        }

        public void setData(ArrayList<Datum> data) {
            this.data = data;
        }

        public String getFirst_page_url() {
            return first_page_url;
        }

        public void setFirst_page_url(String first_page_url) {
            this.first_page_url = first_page_url;
        }

        public int getFrom() {
            return from;
        }

        public void setFrom(int from) {
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

        public ArrayList<Link> getLinks() {
            return links;
        }

        public void setLinks(ArrayList<Link> links) {
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

        public int getMyto() {
            return myto;
        }

        public void setMyto(int myto) {
            this.myto = myto;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int current_page;
        public ArrayList<Datum> data;
        public String first_page_url;
        public int from;
        public int last_page;
        public String last_page_url;
        public ArrayList<Link> links;
        public String next_page_url;
        public String path;
        public int per_page;
        public String prev_page_url;
        @JsonProperty("to")
        public int myto;
        public int total;
    }

    public static class Datum {
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

        public int getMenu_id() {
            return menu_id;
        }

        public void setMenu_id(int menu_id) {
            this.menu_id = menu_id;
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

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public int getQuantity_less() {
            return quantity_less;
        }

        public void setQuantity_less(int quantity_less) {
            this.quantity_less = quantity_less;
        }

        public int getVeg() {
            return veg;
        }

        public void setVeg(int veg) {
            this.veg = veg;
        }

        public int getOrderable() {
            return orderable;
        }

        public void setOrderable(int orderable) {
            this.orderable = orderable;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public int getPriority() {
            return priority;
        }

        public void setPriority(int priority) {
            this.priority = priority;
        }

        public int getFeatured() {
            return featured;
        }

        public void setFeatured(int featured) {
            this.featured = featured;
        }

        public int getTopselling() {
            return topselling;
        }

        public void setTopselling(int topselling) {
            this.topselling = topselling;
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

        public int id;
        public int shop_id;
        public int menu_id;
        public String name;
        public String image;
        public String amount;
        public String offer_price;
        public String description;
        public int quantity;
        public int quantity_less;
        public int veg;
        public int orderable;
        public int status;
        public int priority;
        public int featured;
        public int topselling;
        public String created_at;
        public String updated_at;
        public String deleted_at;
        public int user_item_rating;
        public String average_rating;
        public String image_path;
    }

    public static class Link {
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

        public String url;
        public String label;
        public boolean active;
    }
}