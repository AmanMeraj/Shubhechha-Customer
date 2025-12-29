package com.subh.shubhechha.Model;
import java.util.List;

public class NotificationResponse {
    private int status;
    private String message;
    private Data data;

    // Getters & Setters
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

    public class Data {

        private Notifications notifications;

        public Notifications getNotifications() {
            return notifications;
        }

        public void setNotifications(Notifications notifications) {
            this.notifications = notifications;
        }
    }


    public class Notifications {

        private int current_page;
        private List<NotificationItem> data;
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

        // Getters & Setters
        public int getCurrent_page() {
            return current_page;
        }

        public void setCurrent_page(int current_page) {
            this.current_page = current_page;
        }

        public List<NotificationItem> getData() {
            return data;
        }

        public void setData(List<NotificationItem> data) {
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
    public class NotificationItem {

        private int id;
        private String title;
        private String description;
        private String created_at;

        // Getters & Setters
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getCreated_at() {
            return created_at;
        }

        public void setCreated_at(String created_at) {
            this.created_at = created_at;
        }
    }
    public class PaginationLink {

        private String url;
        private String label;
        private boolean active;

        // Getters & Setters
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
