package com.subh.shubhechha.Model;

public class CartItem {
    private String productId;
    private String productName;
    private String productDescription;
    private String imageUrl;
    private double originalPrice;
    private double currentPrice;
    private int quantity;
    private int maxQuantity;

    // Constructor
    public CartItem(String productId, String productName, String productDescription,
                    String imageUrl, double originalPrice, double currentPrice,
                    int quantity, int maxQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.productDescription = productDescription;
        this.imageUrl = imageUrl;
        this.originalPrice = originalPrice;
        this.currentPrice = currentPrice;
        this.quantity = quantity;
        this.maxQuantity = maxQuantity;
    }

    // Getters
    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductDescription() {
        return productDescription;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public double getOriginalPrice() {
        return originalPrice;
    }

    public double getCurrentPrice() {
        return currentPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    // Setters
    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setProductDescription(String productDescription) {
        this.productDescription = productDescription;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setOriginalPrice(double originalPrice) {
        this.originalPrice = originalPrice;
    }

    public void setCurrentPrice(double currentPrice) {
        this.currentPrice = currentPrice;
    }

    public void setQuantity(int quantity) {
        this.quantity = Math.max(1, Math.min(quantity, maxQuantity));
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    // Utility methods
    public void incrementQuantity() {
        if (quantity < maxQuantity) {
            quantity++;
        }
    }

    public void decrementQuantity() {
        if (quantity > 1) {
            quantity--;
        }
    }

    public boolean canIncrement() {
        return quantity < maxQuantity;
    }

    public boolean canDecrement() {
        return quantity > 1;
    }

    public double getTotalPrice() {
        return currentPrice * quantity;
    }

    public double getSavings() {
        return (originalPrice - currentPrice) * quantity;
    }
}