package com.example.tkarl.newlab2;

import android.content.SharedPreferences;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

public class ArchiveViewActivity extends BaseActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    SharedPreferences settings;
  String userName;
   String passWord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (android.os.Build.VERSION.SDK_INT > 9)
        {
            StrictMode.ThreadPolicy policy =
                    new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        setContentView(R.layout.activity_archive_view);
        settings = PreferenceManager.getDefaultSharedPreferences(this);
        settings.registerOnSharedPreferenceChangeListener(this);
        archiveListShow();

    }

    public void archiveListShow(){
        BufferedReader in = null;
        ArrayList<HashMap<String,String>> archiveItems = new ArrayList();
        ListView archiveListView = (ListView)findViewById(R.id.archive_list_view);
        try
        {
            userName = settings.getString("user_name","username");
            passWord = settings.getString("password","password");
            HttpClient client = new DefaultHttpClient();
            HttpGet request = new HttpGet();
            request.setURI(new URI("http://www.youcode.ca/Lab02Get.jsp?ALIAS="+userName+"&PASSWORD="+passWord));
            HttpResponse response = client.execute(request);
            in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
            String line = "";
            while((line = in.readLine()) != null)
            {


                HashMap<String,String> temp = new HashMap<>();

                    temp.put("CREATED_DATE", line);
                    line = in.readLine();
                    temp.put("LIST_TITLE", line);
                    line =in.readLine();
                    temp.put("CONTENT", line);
                    line = in.readLine();
                    temp.put("COMPLETED_FLAG",line);

                    archiveItems.add(temp);


            }
            in.close();

            String[] keys = {"CONTENT","CREATED_DATE","LIST_TITLE","COMPLETED_FLAG"};
            int[] ids = {R.id.Archive_Item_Display_Name,R.id.Archive_Item_Display_date,R.id.Archive_Item_Display_Title,R.id.Archive_Item_Display_Flag};
            SimpleAdapter adapter = new SimpleAdapter(this,archiveItems,R.layout.archive_item_row,keys,ids);
           archiveListView.setAdapter(adapter);

        }
        catch(Exception e)
        {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        userName = settings.getString("user_name","username");
        passWord = settings.getString("password","password");
    }
}

