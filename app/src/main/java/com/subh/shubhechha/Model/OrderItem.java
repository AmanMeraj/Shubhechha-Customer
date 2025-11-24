package com.subh.shubhechha.Model;

public class OrderItem {
    private int quantity;
    private String itemName;
    private double itemPrice;

    public OrderItem() {
    }

    public OrderItem(int quantity, String itemName, double itemPrice) {
        this.quantity = quantity;
        this.itemName = itemName;
        this.itemPrice = itemPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public double getItemPrice() {
        return itemPrice;
    }

    public void setItemPrice(double itemPrice) {
        this.itemPrice = itemPrice;
    }
}