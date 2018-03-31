package com.example.tkarl.newlab2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by tkarl on 3/30/2018.
 */

public class DBManager extends SQLiteOpenHelper {


    static final String TAG = "DBManager";
    static final String DB_NAME = "Lists.db";
    static final int DB_VERSION = 2;
    //List table
    static final String L_TABLE = "lists";
    static final String L_ID = "ListID";
    static final String L_DATE = "listDate";
    static final String L_ListName = "message";
    //Item Table
    static final String I_TABLE = "items";
    static final String I_ID = "ItemID";
    static final String I_DATE = "ItemDate";
    static final String I_STATUS = "ItemStatus";
    static final String I_ITEM = "item";
    static final String I_LIST= "ListID";

    static final String ID_AUTO = " INTEGER PRIMARY KEY AUTOINCREMENT";



    public DBManager(Context context)
    {
        super(context,DB_NAME,null,DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {

        String listSql = "create table " + L_TABLE + " (" + L_ID +  ID_AUTO + " , "
                + L_DATE + " text, " + L_ListName + " text)";

        String itemSql = "create table " + I_TABLE + " ( " + I_ID + ID_AUTO + " , "
                + I_ITEM + " text, " + I_DATE + " text, " + I_STATUS + " INTEGER DEFAULT 0, " + I_LIST + " int)";

        database.execSQL(listSql);
        database.execSQL(itemSql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("drop table if exists " + L_TABLE); // drops the old database
        database.execSQL("drop table if exists " + I_TABLE);
        Log.d(TAG, "onUpgrade: executed");
        onCreate(database);
    }
}