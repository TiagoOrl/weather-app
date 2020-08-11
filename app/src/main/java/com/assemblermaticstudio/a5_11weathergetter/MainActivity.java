package com.assemblermaticstudio.a5_11weathergetter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;

public class MainActivity extends AppCompatActivity {


    // threads and getter objects
    ExecutorService executorService = Executors.newFixedThreadPool(10);
    JSONGetterTask jsonGetter;
    LocationManager locationManager;
    InputMethodManager inputMethodManager;

    // Views
    TextView tv_latitude, tv_longitude, tv_city, tv_weather, tv_temperature;
    EditText et_cityInput;
    Button btn_cityInput;
    FloatingActionButton fab_getThroughLocation;
    ConstraintLayout cl_data;
    RecyclerView rv_forecastDisplay;
    RecyclerView.Adapter mForecastAdapter;
    RecyclerView.LayoutManager mForecastLayoutManager;
    ImageView iv_weatherIcon;
    AdView ad_main;

    // arrays and consts
    ArrayList<ForecastDayItem> mForecastDayList;
    static final String API_KEY = "375d0d17583d557e8c95485fc73b0e94";
    static final float F_ANIM_POSX = 1200f;
    static final int FINE_LOCATION_CODE = 1;
    double d_latitude, d_longitude;

    private final LocationListener locationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            d_longitude = location.getLongitude();
            d_latitude = location.getLatitude();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
        }

        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(MainActivity.this, R.string.gps_warning, Toast.LENGTH_LONG).show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        tv_latitude = findViewById(R.id.tv_latitude);
        tv_longitude = findViewById(R.id.tv_longitude);
        tv_weather = findViewById(R.id.tv_weather);
        tv_city = findViewById(R.id.tv_city);
        tv_temperature = findViewById(R.id.tv_temperature);
        et_cityInput = findViewById(R.id.et_cityInput);
        btn_cityInput = findViewById(R.id.btn_cityInput);
        cl_data = findViewById(R.id.cl_data);
        rv_forecastDisplay = findViewById(R.id.rv_forecast);
        mForecastLayoutManager = new LinearLayoutManager(this);
        iv_weatherIcon = findViewById(R.id.iv_weatherIcon);
        fab_getThroughLocation = findViewById(R.id.fab_location);
        ad_main = findViewById(R.id.ad_main);

        inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(et_cityInput.getWindowToken(), 0);

        AdRequest adRequest = new AdRequest.Builder().build();
        ad_main.loadAd(adRequest);


        cl_data.animate().translationX(F_ANIM_POSX).alpha(0.0f).setDuration(0);
        rv_forecastDisplay.animate().alpha(0.0f).setDuration(0);


        if(checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            locationManager = (LocationManager)MainActivity.this.getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 10, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);


        } else {
            if (shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(MainActivity.this, R.string.request_warning,
                    Toast.LENGTH_SHORT).show();
            }
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_CODE);
        }
    }

    public void onGetThroughTextInput(View v){

        String s_city = et_cityInput.getText().toString();
        final Handler handler = new Handler();

        // esconder teclado chamado por editText
        InputMethodManager inputMethodManager = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(et_cityInput.getWindowToken(), 0);


        jsonGetter = new JSONGetterTask("https://api.openweathermap.org/data/2.5/forecast?q="+ s_city +"&units=metric&appid=" + API_KEY);
        final Future<JSONObject> future = executorService.submit(jsonGetter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getAndDisplayData(future.get());
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();


    }


    @SuppressLint("MissingPermission")
    public void onGetThroughGPS(View v){


        // esconder teclado chamado por editText
        inputMethodManager.hideSoftInputFromWindow(et_cityInput.getWindowToken(), 0);
        jsonGetter = new JSONGetterTask("https://api.openweathermap.org/data/2.5/forecast?lat=" + d_latitude + "&lon=" + d_longitude + "&units=metric&appid=" + API_KEY);
        final Future<JSONObject> future = executorService.submit(jsonGetter);
        final Handler handler = new Handler();

        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            getAndDisplayData(future.get());
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }).start();
    }

    void getAndDisplayData(JSONObject json_object){
        try {

            if (json_object == null){
                Toast.makeText(MainActivity.this, R.string.return_warning, Toast.LENGTH_SHORT).show();
                return;
            }

            JSONObject json_todayData = json_object.getJSONArray("list").getJSONObject(0);
            JSONObject json_cityData = json_object.getJSONObject("city");
            JSONArray json_forecastArray = json_object.getJSONArray("list");
            mForecastDayList = new ArrayList<>();
            String[] h = new String[9];

            tv_city.setText(MessageFormat.format("{0}, {1}",json_cityData.getString("name"), json_cityData.getString("country") ));
            tv_temperature.setText(MessageFormat.format("{0} ºC",json_todayData.getJSONObject("main").getString("temp") ));
            tv_weather.setText(translator(json_todayData.getJSONArray("weather").getJSONObject(0).getString("description")));
            tv_latitude.setText(MessageFormat.format("Lat: {0}", json_cityData.getJSONObject("coord").getString("lat") ));
            tv_longitude.setText(MessageFormat.format("Lon: {0}", json_cityData.getJSONObject("coord").getString("lon") ));

            iv_weatherIcon.setImageBitmap(setWeatherIcon(json_todayData.getJSONArray("weather").getJSONObject(0).getString("description")));


            for(int i = 0;i < json_forecastArray.length(); i++){
                JSONObject jsonObjIndex = json_forecastArray.getJSONObject(i);

                if (i == 0){
                    h[0] = convertToDDMMFormat(jsonObjIndex.getString("dt_txt").split(" ")[0].split("^\\d+-")[1]) + "\n";
                    i++;
                }


                if (i % 8 == 0){
                    mForecastDayList.add( new ForecastDayItem(h[0], h[1], h[2], h[3], h[4], h[5], h[6], h[7], h[8] ));
                    h = new String[9];
                    h[0] = convertToDDMMFormat(jsonObjIndex.getString("dt_txt").split(" ")[0].split("^\\d+-")[1]) + "\n";
                }

                else {
                    h[i % 8] = jsonObjIndex.getJSONObject("main").getString("temp").split("\\.")[0] + " ºC - "
                            + translator(jsonObjIndex.getJSONArray("weather").getJSONObject(0).getString("main")) + " "
                            +  jsonObjIndex.getString("dt_txt").split(" ")[1].split(":")[0] + "h" + "\n";
                }
            }

            mForecastAdapter = new ForecastViewAdapter(mForecastDayList);
            rv_forecastDisplay.setLayoutManager(mForecastLayoutManager);
            rv_forecastDisplay.setAdapter(mForecastAdapter);

            cl_data.animate().translationX(0.0f).alpha(1.0f).setDuration(250);
            rv_forecastDisplay.animate().alpha(1.0f).setDuration(300);


        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this,R.string.return_warning, Toast.LENGTH_SHORT).show();
        }
    }

    private String translator(String weather){

        String translated = "";

        if (!Locale.getDefault().getLanguage().equals("pt"))
            return weather;

        switch (weather.toLowerCase()) {

            case "rain": translated = "Chuva"; break;
            case "clouds": translated = "Nublado"; break;
            case "clear": translated = "Céu Limpo"; break;
            case "clear sky": translated = "Céu Limpo"; break;
            case "light rain": translated = "Chuva Leve"; break;
            case "scattered clouds": translated = "Nuvens Esparsas"; break;
            case "overcast clouds": translated = "Nuvens Carregadas"; break;
            case "broken clouds": translated = "Nuvens Quebradas"; break;

            default: translated = "null";
        }
        return translated;
    }

    private Bitmap setWeatherIcon(String weather){
        Bitmap bitmap = null;

        switch(weather.toLowerCase()){
            case "clouds": bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cloudy); break;
            case "clear": bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clear_sky); break;
            case "clear sky": bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.clear_sky); break;
            case "light rain": bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rain); break;
            case "rain": bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rain); break;
            case "scattered clouds": bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.scattered_clouds); break;
            case "overcast clouds": bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.overcast_clouds); break;
            case "broken clouds": bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.broken_clouds); break;

            default: bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.cloudy);
        }

        return bitmap;
    }

    private String convertToDDMMFormat(String p){

        String[] date = p.split("-");
        return date[1]+"/"+date[0];
    }
}
