
package com.example.tkarl.newlab2;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,ListView.OnItemSelectedListener {
    private static final String TAG = "TAG";
    private static DBManager dbManager;
    private static SQLiteDatabase database;
    private static ArrayList<List> listArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listArray = new ArrayList<List>();
        Button addList = (Button)findViewById(R.id.Add_List_Button);
        Button delList = (Button)findViewById(R.id.Delete_List_Button);
        addList.setOnClickListener(this);
        delList.setOnClickListener(this);

    }
    //Populate the list items into an adapter
    private void populateLists(){

    }
    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.Add_List_Button:
                EditText editText =(EditText)findViewById(R.id.Edit_Text_List_Name);
                dbManager = new DBManager(this);
                database = dbManager.getWritableDatabase();
                if(!editText.getText().toString().isEmpty() || !editText.getText().toString().trim().equals(""))
                { //start of editText empty check
                    try{
                       String listName =editText.getText().toString();

                        //create a new list.
                        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.CANADA);
                        Date date = new Date();
                        date.getDate();
                        String TodayDate = df.format(date);
                        ContentValues newListValues = new ContentValues();
                        newListValues.put(dbManager.L_ListName,listName);
                        newListValues.put(dbManager.L_DATE,TodayDate);
                        database.insert(dbManager.L_TABLE,null,newListValues);
                        Toast.makeText(this, "List inserted", Toast.LENGTH_SHORT).show();

                    }
                    catch (Exception e){
                        Log.d(TAG, "onClick: "+e.getMessage());
                        Toast.makeText(this, "Error "+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    populateList();
                }//end of editText empty check.
                break;
            case R.id.Delete_List_Button:
                break;
        }
    }

    public void populateList()
    {
            database = dbManager.getReadableDatabase();
           Cursor listsCursor =  database.query(dbManager.L_TABLE,null,null,null,null,null,dbManager.L_ID);

           while(listsCursor.moveToNext()){
               List newList = new List(listsCursor.getInt(0),listsCursor.getString(1),listsCursor.getString(2));

              listArray.add(newList);
               Toast.makeText(this,  "has item " + listsCursor.getString(2), Toast.LENGTH_SHORT).show();
           }
           listsCursor.close();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
