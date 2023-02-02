package com.example.my_find_restaurant_java_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
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
import android.widget.Toast;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {

    OkHttpClient client = new OkHttpClient().newBuilder().build();
    MyFunction callfunction = new MyFunction();
    private ListView lvShow;
    //定位
    private LocationManager locationManager;
    private LocationListener locationListener;
    private String provider;
    public  List<String> list;
    //建立分類陣列
    private String[] fruit_name=new String[]{"牛排","麵條","飯","自助餐","咖啡","甜品","都可以"};
    private FusedLocationProviderClient fusedLocationClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.e("main",callfunction.test());
        lvShow = findViewById(R.id.listview);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }

        //取定位服務
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        list = locationManager.getProviders(true);
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        //監聽位置
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (location != null) {
                    // Logic to handle location object
                    String address = "緯度："+location.getLatitude()+"經度："+location.getLongitude();
                    Log.e("main","現在位置:"+address);
                }
            }
        };

        //startActivity(intent);
    }
    @Override
    protected  void onResume()
    {
        super.onResume();
        Log.e("main","resume");
        //檢查權限，並取得當前位置

        //新增一個Adapter
        ArrayAdapter<String> adapter=
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,fruit_name);//展示地點,格式,字串
        lvShow.setAdapter(adapter);
        lvShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //取得有關分類JSON格式的資料
                //都可以->只要分類是餐廳都拿回來
               /* if(fruit_name[position].equals("都可以"))
                {

                    Request request = new Request.Builder()
                            .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
                            .build();
                }
                else
                {
                    Request request = new Request.Builder()
                            .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json")
                            .build();
                }*/
                Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("targeting",fruit_name[position]);
                startActivity(intent);
            }
        });

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.e("onRequestPermissionsResult",Integer.toString(requestCode)+","+permissions[0]+","+grantResults[0]);
        Log.e("onRequestPermissionsResult",Integer.toString(requestCode)+","+permissions[1]+","+grantResults[1]);
        if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
                ||checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (list.contains(LocationManager.GPS_PROVIDER)) {
                provider = LocationManager.GPS_PROVIDER;
            } else if (list.contains(LocationManager.NETWORK_PROVIDER)) {
                provider = LocationManager.NETWORK_PROVIDER;
            } else {
                Toast.makeText(this, "請打開GPS定位或網路連線", Toast.LENGTH_LONG).show();
            }
            Log.e("main","現在使用的是:"+provider);
            //每分鐘更新定位
            locationManager.requestLocationUpdates(provider,60000,0,locationListener);




        }
    }
    private void getLocal() {

    }


}