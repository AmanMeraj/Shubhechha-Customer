package com.subh.shubhechha.Model;

import java.util.ArrayList;

public class OrderDetails {
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

    public class Data{
        public Order getOrder() {
            return order;
        }

        public void setOrder(Order order) {
            this.order = order;
        }

        public Order order;
    }

    public class Order{
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

        public int getAssign_to() {
            return assign_to;
        }

        public void setAssign_to(int assign_to) {
            this.assign_to = assign_to;
        }

        public int getUser_id() {
            return user_id;
        }

        public void setUser_id(int user_id) {
            this.user_id = user_id;
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

        public int getAddress_id() {
            return address_id;
        }

        public void setAddress_id(int address_id) {
            this.address_id = address_id;
        }

        public String getSubtotal() {
            return subtotal;
        }

        public void setSubtotal(String subtotal) {
            this.subtotal = subtotal;
        }

        public String getTotal() {
            return total;
        }

        public void setTotal(String total) {
            this.total = total;
        }

        public String getTax() {
            return tax;
        }

        public void setTax(String tax) {
            this.tax = tax;
        }

        public String getGst_on_item_total() {
            return gst_on_item_total;
        }

        public void setGst_on_item_total(String gst_on_item_total) {
            this.gst_on_item_total = gst_on_item_total;
        }

        public String getGst_on_packaging_charge() {
            return gst_on_packaging_charge;
        }

        public void setGst_on_packaging_charge(String gst_on_packaging_charge) {
            this.gst_on_packaging_charge = gst_on_packaging_charge;
        }

        public String getGst_on_delivery_charge() {
            return gst_on_delivery_charge;
        }

        public void setGst_on_delivery_charge(String gst_on_delivery_charge) {
            this.gst_on_delivery_charge = gst_on_delivery_charge;
        }

        public String getDelivery_charge() {
            return delivery_charge;
        }

        public void setDelivery_charge(String delivery_charge) {
            this.delivery_charge = delivery_charge;
        }

        public String getPackaging_charge() {
            return packaging_charge;
        }

        public void setPackaging_charge(String packaging_charge) {
            this.packaging_charge = packaging_charge;
        }

        public String getDiscount_amount() {
            return discount_amount;
        }

        public void setDiscount_amount(String discount_amount) {
            this.discount_amount = discount_amount;
        }

        public String getCoupon() {
            return coupon;
        }

        public void setCoupon(String coupon) {
            this.coupon = coupon;
        }

        public String getPromo_id() {
            return promo_id;
        }

        public void setPromo_id(String promo_id) {
            this.promo_id = promo_id;
        }

        public String getCashback_id() {
            return cashback_id;
        }

        public void setCashback_id(String cashback_id) {
            this.cashback_id = cashback_id;
        }

        public String getCashback_amount() {
            return cashback_amount;
        }

        public void setCashback_amount(String cashback_amount) {
            this.cashback_amount = cashback_amount;
        }

        public String getOrder_type() {
            return order_type;
        }

        public void setOrder_type(String order_type) {
            this.order_type = order_type;
        }

        public String getPay_wallet() {
            return pay_wallet;
        }

        public void setPay_wallet(String pay_wallet) {
            this.pay_wallet = pay_wallet;
        }

        public String getPay_amount() {
            return pay_amount;
        }

        public void setPay_amount(String pay_amount) {
            this.pay_amount = pay_amount;
        }

        public String getOrder_note() {
            return order_note;
        }

        public void setOrder_note(String order_note) {
            this.order_note = order_note;
        }

        public int getSelfpick() {
            return selfpick;
        }

        public void setSelfpick(int selfpick) {
            this.selfpick = selfpick;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public String getPay_status() {
            return pay_status;
        }

        public void setPay_status(String pay_status) {
            this.pay_status = pay_status;
        }

        public String getDelivery_time() {
            return delivery_time;
        }

        public void setDelivery_time(String delivery_time) {
            this.delivery_time = delivery_time;
        }

        public String getOtp() {
            return otp;
        }

        public void setOtp(String otp) {
            this.otp = otp;
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

        public Object getDeleted_at() {
            return deleted_at;
        }

        public void setDeleted_at(Object deleted_at) {
            this.deleted_at = deleted_at;
        }

        public String getShop_name() {
            return shop_name;
        }

        public void setShop_name(String shop_name) {
            this.shop_name = shop_name;
        }

        public String getShop_mobile() {
            return shop_mobile;
        }

        public void setShop_mobile(String shop_mobile) {
            this.shop_mobile = shop_mobile;
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

        public ArrayList<Ordritem> getOrdritems() {
            return ordritems;
        }

        public void setOrdritems(ArrayList<Ordritem> ordritems) {
            this.ordritems = ordritems;
        }

        public int id;
        public String orderno;
        public int assign_to;
        public int user_id;
        public int shop_id;
        public int vendor_id;
        public int address_id;
        public String subtotal;
        public String total;
        public String tax;
        public String gst_on_item_total;
        public String gst_on_packaging_charge;
        public String gst_on_delivery_charge;
        public String delivery_charge;
        public String packaging_charge;
        public String discount_amount;
        public String coupon;
        public String promo_id;
        public String cashback_id;
        public String cashback_amount;
        public String order_type;
        public String pay_wallet;
        public String pay_amount;
        public String order_note;
        public int selfpick;
        public String status;
        public String pay_status;
        public String delivery_time;
        public String otp;
        public String created_at;
        public String updated_at;
        public Object deleted_at;
        public String shop_name;
        public String shop_mobile;
        public String address;
        public String pincode;
        public String receipent_name;
        public String user_mobile;
        public String user_image;
        public String shop_image;
        public ArrayList<Ordritem> ordritems;
    }

    public class Ordritem{
        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getOrder_id() {
            return order_id;
        }

        public void setOrder_id(int order_id) {
            this.order_id = order_id;
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

        public String getAmount() {
            return amount;
        }

        public void setAmount(String amount) {
            this.amount = amount;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
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

        public String getImage_path() {
            return image_path;
        }

        public void setImage_path(String image_path) {
            this.image_path = image_path;
        }

        public int id;
        public int order_id;
        public int item_id;
        public int shop_id;
        public String amount;
        public int quantity;
        public String name;
        public String image;
        public String created_at;
        public String updated_at;
        public String image_path;
    }
}
