package com.example.my_find_restaurant_java_app;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
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

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import android.Manifest;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
    private String[] search_name=new String[]{"牛排","麵條","飯","自助餐","咖啡","甜品","都可以"};
    private FusedLocationProviderClient fusedLocationClient;

    //搜索
    public String pos;
    public String radius = "500";
    public String type  = "restaurant";
    private String api_key ;

    //get MAP_API value
    private  String getMetaDataFromApp()
    {
        String value = "";
        try {
            ApplicationInfo appinfo = getPackageManager().getApplicationInfo(getPackageName(),
                    PackageManager.GET_META_DATA);
            value = appinfo.metaData.getString("com.google.android.geo.API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("main","ERROR to get key");
            e.printStackTrace();
        }
        return  value;
    }


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

        //監聽位置
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if (location != null) {
                    // Logic to handle location object
                    String address = "緯度："+location.getLatitude()+"經度："+location.getLongitude();
                    pos = location.getLatitude()+","+location.getLongitude();
                    Log.e("main","現在位置:"+address);
                }
            }
            @Override
            public void onProviderEnabled(String provider) {
                Log.e("main","開始服務");
            }
            @Override
            public void onProviderDisabled(String provider) {
                Log.e("main","未開始服務");
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
        api_key = getMetaDataFromApp();

        //新增一個Adapter


        ArrayAdapter<String> adapter=
                new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,search_name);//展示地點,格式,字串
        lvShow.setAdapter(adapter);
        lvShow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {


                //取得有關分類JSON格式的資料
                //都可以->只要分類是餐廳都拿回來
                Request request;
                //search by using Place Search API
                if(search_name[position].equals("都可以"))
                {

                     request = new Request.Builder()
                            .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+pos+"&radius="+radius+"&type="+type+"&keyword="+"餐廳"+"&key="+api_key)
                            .build();
                }
                else
                {
                     request = new Request.Builder()
                            .url("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="+pos+"&radius="+radius+"&type="+type+"&keyword="+"餐廳+"+search_name[position]+"&key="+api_key)
                            .build();
                }
                Call call = client.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        // 連線成功，自response取得連線結果
                        String result = response.body().string();
                        JSONObject Jobject = null;
                        //String to json
                        try {
                            Jobject = new JSONObject(result);
                            //Log.e("GSON result", Jobject.get("results").toString());
                            JSONArray Jarray = Jobject.getJSONArray("results");
                            //show result if there are at least one
                            for (int i = 0; i < Jarray.length(); i++) {
                                JSONObject object     = Jarray.getJSONObject(i);
                                //Log.e("test",object.toString());
                            }
                            if(Jarray.length() > 0)
                            {
                                Log.e("Searching result", "There are total "+ Integer.toString(Jarray.length()) + " results.");
                                Intent intent = new Intent(MainActivity.this, ShowRestaurantListScreen.class);
                                intent.putExtra("jsonArray", Jarray.toString());
                                startActivity(intent);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        /*Gson gson = new Gson();
                        Type type = new TypeToken<List<ContactModel>>(){}.getType();
                        List<ContactModel> contactList = gson.fromJson(String.valueOf(response.body()),type);
                        */


                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        // 連線失敗

                    }
                });

                /*Intent intent = new Intent(MainActivity.this,MapsActivity.class);
                intent.putExtra("targeting",fruit_name[position]);
                startActivity(intent);*/
            }
        });

    }

    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
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
            provider = LocationManager.NETWORK_PROVIDER;
            //每分鐘更新定位
            try {
                Log.e("main","開始更新:"+provider);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,10000,0,locationListener);
            }
            catch (Exception e)
            {
                Log.e("main","沒有");
                e.printStackTrace();
            }



        }

    }
    private void getLocal() {

    }


}