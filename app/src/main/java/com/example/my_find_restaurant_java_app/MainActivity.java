package com.example.my_find_restaurant_java_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import okhttp3.OkHttpClient;
public class MainActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient().newBuilder().build();
    MyFunction callfunction = new MyFunction();
    private ListView lvShow;
    //  建立陣列
    private String[] fruit_name=new String[]{"Apple","Banana","Orange","Grape","Strawberry","123456"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("main",callfunction.test());
        lvShow = findViewById(R.id.listview);


        //startActivity(intent);
    }
    @Override
    protected  void onResume()
    {
        super.onResume();
        Log.e("main","resume");
        //新增一個Adapter
        ArrayAdapter<String> adapter=
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,fruit_name);//展示地點,格式,字串
        lvShow.setAdapter(adapter);
        lvShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //印出選擇
                Log.e("main",fruit_name[position]);
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("targeting",fruit_name[position]);
                startActivity(intent);
            }
        });

    }


}