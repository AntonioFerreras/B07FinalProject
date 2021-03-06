package com.example.groceryapp;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class Order implements Serializable {
    private String customer_id;
    private boolean order_sent;
    private String order_store_id;
    private HashMap<String, Integer> items_ids;
    private boolean is_processing = false;
   // private String date;


    public String getCustomer_id() {
        return customer_id;
    }

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public void setOrder_sent(boolean order_sent) {
        this.order_sent = order_sent;
    }

    public String getOrder_store_id() {
        return order_store_id;
    }

    public void setOrder_store_id(String order_store_id) {
        this.order_store_id = order_store_id;
    }

    public HashMap<String, Integer> getItems_ids() {
        return items_ids;
    }

    public void setItems_ids(HashMap<String, Integer> items_ids) {
        this.items_ids = items_ids;
    }

    public boolean isIs_complete() {
        return is_complete;
    }

    public void complete_order() {
        this.is_complete = true;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    private boolean is_complete = false;

    public boolean isIs_processing() {
        return is_processing;
    }

    public void setIs_processing(boolean is_processing) {
        this.is_processing = is_processing;
    }



    @Exclude
    private String id;
    public Order(){

    }

    public Order(String customer_id,boolean order_sent, boolean is_complete, String order_store_id, HashMap<String, Integer> items_ids){
        this.customer_id = customer_id;
        this.items_ids = items_ids;
        this.order_store_id = order_store_id;
        this.is_complete = is_processing;
        this.is_complete = is_complete;
        // Get current time
        //this.date = date;
        //DBConstants.dateFormat.format(Calendar.getInstance().getTime());
    }




    //remove item from the Hashtable:
    //if the order quantity is ZERO:


}
