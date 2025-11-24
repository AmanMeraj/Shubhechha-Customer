package com.subh.shubhechha.Model;

public class ShopItem {
    private String id;
    private String name;
    private String description;
    private double originalPrice;
    private double currentPrice;
    private int imageResId;
    private int quantity;
    private boolean isInCart;

    // Constructor
    public ShopItem(String id, String name, String description,
                    double originalPrice, double currentPrice,
                    int imageResId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.originalPrice = originalPrice;
        this.currentPrice = currentPrice;
        this.imageResId = imageResId;
        this.quantity = 0;
        this.isInCart = false;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public int getImageResId() {
        return imageResId;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isInCart() {
        return isInCart;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setImageResId(int imageResId) {
        this.imageResId = imageResId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void setInCart(boolean inCart) {
        isInCart = inCart;
    }

    // Utility methods
    public void incrementQuantity() {
        this.quantity++;
        this.isInCart = true;
    }

    public void decrementQuantity() {
        if (this.quantity > 0) {
            this.quantity--;
            if (this.quantity == 0) {
                this.isInCart = false;
            }
        }
    }

    public double getTotalPrice() {
        return currentPrice * quantity;
    }

    public int getDiscountPercentage() {
        if (originalPrice <= 0) return 0;
        return (int) (((originalPrice - currentPrice) / originalPrice) * 100);
    }
}
