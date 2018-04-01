
package com.example.tkarl.newlab2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,ListView.OnItemClickListener {
    private static final String TAG = "TAG";
    private static DBManager dbManager;
    private static SQLiteDatabase database;
    private static ArrayList<List> listArray;
    private static ArrayList<HashMap<String,String>> HashList;
    private static ListView listDisplay;
    private static SimpleAdapter adapter;
    private static Integer chosenID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listArray = new ArrayList<List>();
        HashList = new ArrayList<HashMap<String, String>>();
        Button addList = (Button)findViewById(R.id.Add_List_Button);
        Button delList = (Button)findViewById(R.id.Delete_List_Button);
        Button viewList = (Button)findViewById(R.id.View_Items_List_Button);
        addList.setOnClickListener(this);
        delList.setOnClickListener(this);
        viewList.setOnClickListener(this);

        listDisplay = (ListView)findViewById(R.id.List_Display_View);
        HashList = new ArrayList<HashMap<String, String>>();
        dbManager = new DBManager(this);
       populateList();
       listDisplay.setOnItemClickListener(this);



    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.Add_List_Button:
                EditText editText =(EditText)findViewById(R.id.Edit_Text_List_Name);
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

                if(chosenID != null)
                {//start of check if item is selected
                    try {

                        int listID = listArray.get(chosenID).getId();
                       String listName = listArray.get(chosenID).getListName();
                        database = dbManager.getWritableDatabase();
                        database.delete(dbManager.L_TABLE,dbManager.L_ID +" = "+listID,null);
                        chosenID = null;
                        Toast.makeText(this, listName+" Deleted", Toast.LENGTH_SHORT).show();
                        populateList();
                    }
                    catch (Exception e){
                        Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(this, "No item selected to delete...", Toast.LENGTH_SHORT).show();
                }//of of check if item is selected
                break;

            case R.id.View_Items_List_Button:
                if(chosenID != null) {//start of check if item is selected
                    int listID = listArray.get(chosenID).getId();
                    String listName = listArray.get(chosenID).getListName();
                    //intent to send to the itemActionActivity.
                    Intent intent = new Intent(this,ItemActionActivity.class);
                    intent.putExtra("ID",listID);
                    startActivity(intent);
                }
                else {
                    Toast.makeText(this, "No item selected to view...", Toast.LENGTH_SHORT).show();
                }//of of check if item is selected
                break;
        }
    }

    //display list items into the listview
    public void populateList()
    {
            HashList.clear();
            listArray.clear();
            database = dbManager.getReadableDatabase();
           Cursor listsCursor =  database.query(dbManager.L_TABLE,null,null,null,null,null,dbManager.L_ID);

           while(listsCursor.moveToNext()){
               HashMap<String,String> temp = new HashMap<String, String>();
               //0 =id, 1 = date 2 =name
               List newList = new List(listsCursor.getInt(0),listsCursor.getString(1),listsCursor.getString(2));
              listArray.add(newList);
               temp.put("NAME",listsCursor.getString(2));
               temp.put("DATE",listsCursor.getString(1));

               HashList.add(temp);

           }
           listsCursor.close();
        String[] keys = new String[]{"NAME","DATE"};
        int[] ids = new int[]{R.id.List_Display_Name,R.id.List_Display_date};
        adapter = new SimpleAdapter(this,HashList,R.layout.list_row,keys,ids);
        listDisplay.setAdapter(adapter);

    }

    //Declares a selected item from the list. Passes it into ChosenID which is a static variable.
    //ChosenID will be used to select an ID when needed in the DeleteList and ViewItems button actions.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for(int i = 0; i < parent.getCount();i++){
            parent.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.WhiteBackground));
        }
        chosenID = position;

        view.setBackgroundColor(getResources().getColor(R.color.SelectedListItemColor));


    }
}
