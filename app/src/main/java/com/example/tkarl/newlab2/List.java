package com.example.tkarl.newlab2;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by tkarl on 3/30/2018.
 */

public class List {

    int id;
    String date;
    String listName;

    public List(int id, String date, String listName) {
        this.id = id;
        this.date = date;
        this.listName = listName;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getListName() {
        return listName;
    }
}
