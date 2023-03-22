package com.example.my_find_restaurant_java_app;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.location.FusedLocationProviderClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

public class ShowRestaurantListScreen extends AppCompatActivity {
    //a Activity for showing the restaurant near by user after they choose what they want to eat

    private ListView lvShow;
    //建立分類陣列
    public String[] search_name;
    private FusedLocationProviderClient fusedLocationClient;
    private String jsonArray;
    //位置記錄
    public Double[] lat;
    public Double[] lng;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //設定展示頁
        setContentView(R.layout.activity_show_restaurant_list_screen);
        lvShow = findViewById(R.id.listview);

        //讀取發過來搜索結果，整理成列表
        Intent intent = getIntent();
        jsonArray = intent.getStringExtra("jsonArray");
        try {

            JSONArray array = new JSONArray(jsonArray);
            //實例化Array=>不然會報錯
            search_name = new String[array.length()];
            lat = new Double[array.length()];
            lng = new Double[array.length()];
            for (int i = 0; i < array.length(); i++) {
                JSONObject object  = array.getJSONObject(i);
                search_name[i] = object.getString("name");
                lat[i] = object.getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                lng[i] = object.getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                Log.e("test",Double.toString(object.getJSONObject("geometry").getJSONObject("location").getDouble("lat")));
                Log.e("test",Double.toString(object.getJSONObject("geometry").getJSONObject("location").getDouble("lng")));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    @Override
    protected  void onResume()
    {
        super.onResume();
        Log.e("main","resume");

        //新增一個Adapter

        ArrayAdapter<String> adapter=
                new ArrayAdapter<String>(ShowRestaurantListScreen.this,android.R.layout.simple_list_item_1,search_name);//展示地點,格式,字串
        lvShow.setAdapter(adapter);
        lvShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //target:按下去後打開地圖，展示選擇的餐廳的位置
                Intent intent = new Intent(ShowRestaurantListScreen.this, MapsActivity.class);
                intent.putExtra("targeting",search_name[position]);
                intent.putExtra("lat",lat[position]);
                intent.putExtra("lng",lng[position]);
                startActivity(intent);
            }
        });
    }
}
