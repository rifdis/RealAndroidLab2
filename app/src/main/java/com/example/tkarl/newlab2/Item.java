package com.example.tkarl.newlab2;

/**
 * Created by tkarl on 3/30/2018.
 */

public class Item {

    int id;
    String date;
    String itemMessage;
    int status;


    public Item(int item_id, String item_date, String itemMessage,int status) {
        this.id = item_id;
        this.date = item_date;
        this.itemMessage = itemMessage;
        this.status = status;



    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }


    public int getStatus() {
        return status;
    }
    public String getItemMessage() {
        return itemMessage;
    }


}
