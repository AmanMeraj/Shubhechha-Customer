package com.subh.shubhechha.Model;

import java.util.ArrayList;

public class AddToCartModel {
    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getForce_add() {
        return force_add;
    }

    public void setForce_add(String force_add) {
        this.force_add = force_add;
    }

    public ArrayList<String> getAdded_modifier_id() {
        return added_modifier_id;
    }

    public void setAdded_modifier_id(ArrayList<String> added_modifier_id) {
        this.added_modifier_id = added_modifier_id;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String item_id;
    public String quantity;
    public String force_add;

    //this is for PUT model
    public String type;
    public ArrayList<String> added_modifier_id;
}
