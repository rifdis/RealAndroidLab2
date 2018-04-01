package com.example.tkarl.newlab2;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.TimeZone;

public class ItemActionActivity extends AppCompatActivity implements View.OnClickListener,ListView.OnItemClickListener{
    private static Item item;
    private static ArrayList<Item> itemArrayList;
    private static ArrayList<HashMap<String,String>> itemHashList;
    private static DBManager dbManager;
    private static SQLiteDatabase database;
    private static SimpleAdapter adapter;
    private static ListView itemListView;
    private static int listID;
    private static Integer chosenID;
    private static EditText newItemEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_action);
        itemListView = (ListView)findViewById(R.id.Item_Display_View);
        itemArrayList = new ArrayList<Item>();
        itemHashList = new ArrayList<HashMap<String, String>>();
        newItemEditText= (EditText)findViewById(R.id.Edit_Text_Item_Name);
        //buttons
        itemListView.setOnItemClickListener(this);
        Button addItem = (Button)findViewById(R.id.Add_Item_Button);
        Button DeleteItem = (Button)findViewById(R.id.Delete_Item_Button);
        Button ArchiveItem = (Button)findViewById(R.id.Archive_Item_Button);
        Button editItem = (Button)findViewById(R.id.Edit_Item_Button);
        addItem.setOnClickListener(this);
        DeleteItem.setOnClickListener(this);
        ArchiveItem.setOnClickListener(this);
        editItem.setOnClickListener(this);
        //end of button declerations

        TextView listHeader = (TextView)findViewById(R.id.List_Name_For_Items_Display);
         listID = getIntent().getIntExtra("ID",0);
        dbManager = new DBManager(this);
        try {
            String[] getName = {dbManager.L_ListName};
            database = dbManager.getReadableDatabase();
            Cursor listTitle = database.query(dbManager.L_TABLE,getName,dbManager.L_ID +"="+listID,null,null,null,null);
            listTitle.moveToFirst();
            listHeader.setText(listTitle.getString(0));
        }
        catch (Exception e){

        }
    populateItems();
    }

    public void populateItems()
    {
     itemHashList.clear();
     itemArrayList.clear();
     database = dbManager.getReadableDatabase();
    Cursor itemCursor = database.query(dbManager.I_TABLE,null,dbManager.I_LIST +"="+listID,null,null,null,dbManager.I_ID);
    while (itemCursor.moveToNext())
    {
        HashMap<String,String> temp = new HashMap<String, String>();
        //0 = id 1 =date 2=name 3 =status
        Item newItem = new Item(itemCursor.getInt(0),itemCursor.getString(2),itemCursor.getString(1),itemCursor.getInt(3));

        temp.put("NAME",newItem.getItemMessage());
        temp.put("DATE",newItem.getDate());
        //temp.put("STATUS",Integer.toString(newItem.getStatus()));
        itemHashList.add(temp);
        itemArrayList.add(newItem);
    }

    String[] keys = {"NAME","DATE"};
    int[] ids = {R.id.Item_Display_Name,R.id.Item_Display_date};
    adapter = new SimpleAdapter(this,itemHashList,R.layout.item_row,keys,ids);
    itemListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId())
        {
            case R.id.Add_Item_Button:
                if(!newItemEditText.getText().toString().isEmpty()){
                database = dbManager.getWritableDatabase();
                DateFormat df = DateFormat.getDateTimeInstance(DateFormat.MEDIUM,DateFormat.SHORT,Locale.CANADA);
                Date date = new Date();
                date.getDate();
                df.setTimeZone(TimeZone.getTimeZone("US/Mountain"));
                String TodayDate = df.format(date);
                ContentValues newItemValues = new ContentValues();
                newItemValues.put(dbManager.I_ITEM,newItemEditText.getText().toString());
                newItemValues.put(dbManager.I_DATE,TodayDate);
                newItemValues.put(dbManager.I_LIST,listID);
                try {
                    database.insert(dbManager.I_TABLE, null, newItemValues);
                }
                catch (Exception e)
                {
                    Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                populateItems();
                }
                else{
                    Toast.makeText(this, "Please enter a name for the item", Toast.LENGTH_SHORT).show();
                }
            break;
            case R.id.Delete_Item_Button:
                database = dbManager.getWritableDatabase();
                int itemChosenID = itemArrayList.get(chosenID).getId();
                String itemChosenName = itemArrayList.get(chosenID).getItemMessage();
                try{

                    database.delete(dbManager.I_TABLE,dbManager.I_ID +" = "+itemChosenID,null);
                    Toast.makeText(this, itemChosenName +" Deleted", Toast.LENGTH_SHORT).show();
                    chosenID = null;
                }
                catch (Exception e)
                {
                    Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                populateItems();
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for(int i = 0; i < parent.getCount();i++){
            parent.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.WhiteBackground));
        }
        chosenID = position;

        view.setBackgroundColor(getResources().getColor(R.color.SelectedListItemColor));

    }
}
