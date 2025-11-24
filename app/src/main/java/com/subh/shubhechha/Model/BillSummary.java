package com.subh.shubhechha.Model;

public class BillSummary {
    private String label;
    private double amount;

    public BillSummary() {
    }

    public BillSummary(String label, double amount) {
        this.label = label;
        this.amount = amount;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }
}