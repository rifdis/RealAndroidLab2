package com.example.tkarl.newlab2;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Text;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.text.DateFormat;
import java.util.*;
import java.util.List;

public class ItemActionActivity extends BaseActivity  implements View.OnClickListener,ListView.OnItemClickListener,SharedPreferences.OnSharedPreferenceChangeListener,CheckBox.OnCheckedChangeListener{
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
    private static int itemChosenID;
    private static String ThisListTitle;
    private static String userName;
    private static String passWord;
    String headerColor;

    View itemRow;
    CheckBox checkBox;

    SharedPreferences settings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_action);

        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(this);
        itemListView = (ListView)findViewById(R.id.Item_Display_View);
        itemListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
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
        String fontSize = settings.getString("Title_Font_Size","45");
        float floatFont = Float.parseFloat(fontSize);
        TextView listHeader = (TextView)findViewById(R.id.List_Name_For_Items_Display);
         listID = getIntent().getIntExtra("ID",0);
           listHeader.setTextSize(floatFont);
        headerColor = settings.getString("main_bg_color_list","#cccccc");
        listHeader.setBackgroundColor(Color.parseColor(headerColor));
        dbManager = new DBManager(this);
        try {
            String[] getName = {dbManager.L_ListName};
            database = dbManager.getReadableDatabase();
            Cursor listTitle = database.query(dbManager.L_TABLE,getName,dbManager.L_ID +"="+listID,null,null,null,null);
            listTitle.moveToFirst();
            listHeader.setText(listTitle.getString(0));
            ThisListTitle = listTitle.getString(0);
        }
        catch (Exception e){

        }
    populateItems();



    }

    public void populateItems() {
        itemHashList.clear();
        itemArrayList.clear();
        database = dbManager.getReadableDatabase();
        Cursor itemCursor = database.query(dbManager.I_TABLE, null, dbManager.I_LIST + "=" + listID, null, null, null, dbManager.I_ID);
        while (itemCursor.moveToNext()) {
            HashMap<String, String> temp = new HashMap<String, String>();
            //0 = id 1 =date 2=name 3 =status
            Item newItem = new Item(itemCursor.getInt(0), itemCursor.getString(2), itemCursor.getString(1), itemCursor.getInt(3));

            temp.put("NAME", newItem.getItemMessage());
            temp.put("DATE", newItem.getDate());
            //temp.put("STATUS",Integer.toString(newItem.getStatus()));
            itemHashList.add(temp);
            itemArrayList.add(newItem);
        }

        String[] keys = {"NAME", "DATE"};
        int[] ids = {R.id.Item_Display_Name, R.id.Item_Display_date};
        adapter = new SimpleAdapter(this, itemHashList, R.layout.item_row, keys, ids);
        itemListView.setAdapter(adapter);

        itemListView.post(new Runnable() {
            @Override
            public void run() {
           setCheckBox();
            }
        });


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
                newItemEditText.setText("");
                populateItems();
                }
                else{
                    Toast.makeText(this, "Please enter a name for the item", Toast.LENGTH_SHORT).show();
                }

            break;
            case R.id.Delete_Item_Button:
            deleteItem();
                break;
            case R.id.Edit_Item_Button:
                database = dbManager.getWritableDatabase();

                String newItemName = newItemEditText.getText().toString();
                if(!newItemName.isEmpty()){
                try{
                    ContentValues newItemValue = new ContentValues();
                    newItemValue.put(dbManager.I_ITEM,newItemName);
                    database.update(dbManager.I_TABLE,newItemValue,dbManager.I_ID +"="+ itemChosenID,null);
                    populateItems();
                    newItemEditText.setText("");
                }
                catch (Exception e){
                    Toast.makeText(this, "Error: "+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                }
                else{
                    newItemEditText.hasFocus();
                    newItemEditText.setHint(R.string.EnterNewItemName);
                    Toast.makeText(this, "Please enter an item", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.Archive_Item_Button:
                ArchiveItem();
                break;
        }
    }
public void deleteItem(){
    database = dbManager.getWritableDatabase();

    String itemChosenName = itemArrayList.get(chosenID).getItemMessage();
    if(!Integer.toString(itemChosenID).isEmpty()){
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
    }
}
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        for(int i = 0; i < parent.getCount();i++){
            parent.getChildAt(i).setBackgroundColor(getResources().getColor(R.color.WhiteBackground));
        }
        chosenID = position;
        itemChosenID = itemArrayList.get(chosenID).getId();
        view.setBackgroundColor(getResources().getColor(R.color.SelectedListItemColor));

    }

    public void ArchiveItem(){

        Item ThisItem = itemArrayList.get(chosenID);

        String ItemName = ThisItem.getItemMessage();
        String ItemDate = ThisItem.getDate();
        String ItemStatus = Integer.toString(ThisItem.getStatus());
         userName = settings.getString("user_name","username");
         passWord = settings.getString("password","password");
        Toast.makeText(this, userName + " "+ passWord, Toast.LENGTH_SHORT).show();

        try {
            HttpClient client = new DefaultHttpClient();
            HttpPost post = new HttpPost("http://www.youcode.ca/Lab02Post.jsp");
            ArrayList<NameValuePair> postParameters = new ArrayList<>();

            postParameters.add(new BasicNameValuePair("LIST_TITLE", ThisListTitle));
            postParameters.add(new BasicNameValuePair("CONTENT", ItemName));
            postParameters.add(new BasicNameValuePair("COMPLETED_FLAG", ItemStatus));
            postParameters.add(new BasicNameValuePair("CREATED_DATE", ItemDate));
            postParameters.add(new BasicNameValuePair("PASSWORD", passWord));
            postParameters.add(new BasicNameValuePair("ALIAS",userName));
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(postParameters);
            post.setEntity(formEntity);
            client.execute(post);

            Toast.makeText(this, ItemName + " Archived", Toast.LENGTH_SHORT).show();
          deleteItem();
        }
        catch (UnsupportedEncodingException e) {
            Toast.makeText(this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } catch (ClientProtocolException e) {
            Toast.makeText(this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Error: " + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
         userName = settings.getString("user_name","username");
         passWord = settings.getString("password","password");
        String fontSize = settings.getString("Title_Font_Size","45");
        float floatFont = Float.parseFloat(fontSize);
        TextView listHeader = (TextView)findViewById(R.id.List_Name_For_Items_Display);
        listHeader.setTextSize(floatFont);
        headerColor = settings.getString("main_bg_color_list","#ccc");
        listHeader.setBackgroundColor(Color.parseColor(headerColor));
    }

    public void setCheckBox(){
       for(int i = 0; i < itemListView.getCount();i++){
           checkBox = (CheckBox)itemListView.getChildAt(i).findViewById(R.id.Item_Checkbox);
           int thisItemStatus = itemArrayList.get(i).getStatus();
           if (thisItemStatus == 1){
               checkBox.setChecked(true);
           }
           checkBox.setOnCheckedChangeListener(this);
           checkBox.setText(Integer.toString(i));



       }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        item= itemArrayList.get(Integer.parseInt(buttonView.getText().toString()));
        int itemID = item.getId();
        database = dbManager.getWritableDatabase();
        if(isChecked){
            try{
            ContentValues newItemValue = new ContentValues();
            newItemValue.put(dbManager.I_STATUS,1);
            database.update(dbManager.I_TABLE,newItemValue,dbManager.I_ID +"="+ itemID,null);
            Toast.makeText(this,  item.getItemMessage() +" set to complete", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        else
        {
            try{
                ContentValues newItemValue = new ContentValues();
                newItemValue.put(dbManager.I_STATUS,0);
                database.update(dbManager.I_TABLE,newItemValue,dbManager.I_ID +"="+ itemID,null);
                Toast.makeText(this,  item.getItemMessage()+" set to incomplete", Toast.LENGTH_SHORT).show();
            }
            catch (Exception e){
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

        }
       populateItems();
    }


}
