package com.subh.shubhechha.Model;

import java.util.List;

public class WalletResponse {

    private int status;
    private String message;
    private Data data;

    // ---------- Getters & Setters ----------
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

    // ===================== INNER CLASSES =====================

    public static class Data {
        private int wallet_amount;
        private Transactions transactions;

        public int getWallet_amount() {
            return wallet_amount;
        }

        public void setWallet_amount(int wallet_amount) {
            this.wallet_amount = wallet_amount;
        }

        public Transactions getTransactions() {
            return transactions;
        }

        public void setTransactions(Transactions transactions) {
            this.transactions = transactions;
        }
    }

    public static class Transactions {
        private int current_page;
        private List<TransactionItem> data;
        private String first_page_url;
        private int from;
        private int last_page;
        private String last_page_url;
        private List<PaginationLink> links;
        private String next_page_url;
        private String path;
        private int per_page;
        private String prev_page_url;
        private int to;
        private int total;

        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }

        public List<TransactionItem> getData() {
            return data;
        }

        public void setData(List<TransactionItem> data) {
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

        public List<PaginationLink> getLinks() {
            return links;
        }

        public void setLinks(List<PaginationLink> links) {
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

        public int getTo() {
            return to;
        }

        public void setTo(int to) {
            this.to = to;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }

    public static class TransactionItem {
        private int id;
        private int user_id;
        private int prev_amount;
        private int amount;
        private int current_amount;
        private String type;
        private String notes;
        private Integer order_id;
        private int created_by;
        private String created_at;
        private String updated_at;

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

        public int getPrev_amount() {
            return prev_amount;
        }

        public void setPrev_amount(int prev_amount) {
            this.prev_amount = prev_amount;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }

        public int getCurrent_amount() {
            return current_amount;
        }

        public void setCurrent_amount(int current_amount) {
            this.current_amount = current_amount;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getNotes() {
            return notes;
        }

        public void setNotes(String notes) {
            this.notes = notes;
        }

        public Integer getOrder_id() {
            return order_id;
        }

        public void setOrder_id(Integer order_id) {
            this.order_id = order_id;
        }

        public int getCreated_by() {
            return created_by;
        }

        public void setCreated_by(int created_by) {
            this.created_by = created_by;
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
    }

    public static class PaginationLink {
        private String url;
        private String label;
        private boolean active;

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
