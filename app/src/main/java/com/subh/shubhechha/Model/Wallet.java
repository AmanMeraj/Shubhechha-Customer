package com.subh.shubhechha.Model;
public class Wallet {
    private String title;
    private String subtitle;
    private String amount;
    private String date;
    private int iconResId;
    private boolean isCredit; // true for credit, false for debit

    public Wallet() {
    }

    public Wallet(String title, String subtitle, String amount, String date, int iconResId, boolean isCredit) {
        this.title = title;
        this.subtitle = subtitle;
        this.amount = amount;
        this.date = date;
        this.iconResId = iconResId;
        this.isCredit = isCredit;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public boolean isCredit() {
        return isCredit;
    }

    public void setCredit(boolean credit) {
        isCredit = credit;
    }
}
