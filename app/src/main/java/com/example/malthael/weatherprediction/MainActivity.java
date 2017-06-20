package com.example.malthael.weatherprediction;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationServices;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MainActivity extends AppCompatActivity implements ConnectionCallbacks, OnConnectionFailedListener{
    //Matthew
    EditText edtCitySearch;
    Button btnSearch, btnNextDays;
    TextView tvCityName, tvCountryName, tvTemp, tvWeatherStatus, tvCloud, tvHumidity, tvWind, tvLastUpdate;
    ImageView imgWeatherIcon;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    String mLatitude;
    String mLongitude;
    public static final int MY_PERMISSIONS_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (mGoogleApiClient == null) {  // if googleApiClient does not exist, create one
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // Check for the access location permission
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // If the app does not have permission, it requests user
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST);
        } else {
            // If the permission successfully granted, get current weather for device's last location
            launchLocation();

        }

        getReferences(); // sets all references for edittexts, button, textview, etc...

        btnSearch.setOnClickListener(new View.OnClickListener() { //click the search button
            @Override
            public void onClick(View v) {
                String city = edtCitySearch.getText().toString();
                if(city.equals("")) { // if there is nothing in there
                    Toast.makeText(MainActivity.this, "Please input a location.", Toast.LENGTH_SHORT).show();

                }else { // if there is data inside
                    getCurrentWeatherData(city);
                }

            }
        });

        btnNextDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) { // onclick for checkNextDays
                Intent intent = new Intent(MainActivity.this, CheckNextDays.class);
                String currentCity = tvCityName.getText().toString().toLowerCase().trim();
                String formattedText = currentCity.replace("city: ","");
                intent.putExtra("cityName", formattedText);
                startActivity(intent); //start checkNextDays
            }
        });
    }

    public void getReferences() { //reference for all txt and buttons
        edtCitySearch = (EditText) findViewById(R.id.edt_city_input);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnNextDays = (Button) findViewById(R.id.btn_nextdays);
        tvCityName = (TextView) findViewById(R.id.tv_city_name);
        tvCountryName = (TextView) findViewById(R.id.tv_country_name);
        tvTemp = (TextView) findViewById(R.id.tv_temp);
        tvWeatherStatus = (TextView) findViewById(R.id.tv_weather_status);
        tvCloud = (TextView) findViewById(R.id.tv_cloud);
        tvHumidity = (TextView) findViewById(R.id.tv_humidity);
        tvWind = (TextView) findViewById(R.id.tv_wind);
        tvLastUpdate = (TextView) findViewById(R.id.tv_last_update);
        imgWeatherIcon = (ImageView) findViewById(R.id.imgv_weather_icon);
    }

    protected void onStart() { //start googleApiClient
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() { //stop googleApiClient
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    //Functions to initialize value for Lat and Lon variables
    public void launchLocation() {
        if (ContextCompat.checkSelfPermission(this, // check for permission
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient); // asks for last location
            if(mGoogleApiClient == null) { // if googleapiclient doesnt exist 2
                Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            }

            if (mLastLocation != null) { // if there is no location, ask for it again
                mLatitude=String.valueOf(mLastLocation.getLatitude());
                mLongitude=String.valueOf(mLastLocation.getLongitude());

                getCurrentWeatherDataBasedOnLatAndLon(mLatitude, mLongitude); // ask for current weather based on lon and lat
            }
        }
    }

    //Function to get current weather of location that user searched. The parameter "data" is location name that user types in search box.
    public void getCurrentWeatherData(String data) {
        // use Volley(a HTTP library) by creating a RequestQueue
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://api.openweathermap.org/data/2.5/weather?q="+data+"&units=metric&appid=f9c2ac0eda679b17d06683868e251e1d";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, // request the string url + "data" which is city name
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String cityName = jsonObject.getString("name");//gets the name of the city
                            tvCityName.setText("City: " + cityName);

                            String dateUpdate = jsonObject.getString("dt"); // sets the last updated time, + converts it to readable time with simpledataformat
                            long l = Long.valueOf(dateUpdate);
                            Date date = new Date(l*1000L);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd HH-mm-ss");
                            String dateUpdateFormatted = simpleDateFormat.format(date);
                            tvLastUpdate.setText("Last update: " + dateUpdateFormatted);

                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather"); // sets the icon based on weather
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            String weatherStatus = jsonObjectWeather.getString("main");
                            tvWeatherStatus.setText(weatherStatus);
                            String icon = jsonObjectWeather.getString("icon");
                            Picasso.with(MainActivity.this).load("http://openweathermap.org/img/w/"+icon+".png").into(imgWeatherIcon);

                            JSONObject jsonObjectMain = jsonObject.getJSONObject("main"); // gets the temperature and humidity of data
                            String temp = jsonObjectMain.getString("temp");
                            String humidity = jsonObjectMain.getString("humidity");
                            Double d = Double.valueOf(temp);
                            String tempFormatted = String.valueOf(d.intValue());
                            tvTemp.setText(tempFormatted+"°C");
                            tvHumidity.setText(humidity+"%");

                            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind"); // gets the wind speed
                            String wind = jsonObjectWind.getString("speed");
                            tvWind.setText(wind+"m/s");

                            JSONObject jsonObjectCloud = jsonObject.getJSONObject("clouds"); // sets cloud percentage
                            String cloud = jsonObjectCloud.getString("all");
                            tvCloud.setText(cloud+"%");

                            JSONObject jsonObjectSys = jsonObject.getJSONObject("sys"); // sets the country
                            String country = jsonObjectSys.getString("country");
                            tvCountryName.setText("Country: " + country);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest); //sends the request for all of the above in order to populate the json
    }

    //Function to get current weather of last location of user's device
    public void getCurrentWeatherDataBasedOnLatAndLon(String lat, String lon) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);// asks for lon and lat from openweather
        String url = "http://api.openweathermap.org/data/2.5/weather?lat="+lat+"&lon="+lon+"&units=metric&appid=f9c2ac0eda679b17d06683868e251e1d";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response); // SAME AS PREVIOUS EXEPT IT IS BASED ON LON AND LAT
                            String cityName = jsonObject.getString("name");
                            tvCityName.setText("City: " + cityName);

                            String dateUpdate = jsonObject.getString("dt");
                            long l = Long.valueOf(dateUpdate);
                            Date date = new Date(l*1000L);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd HH-mm-ss");
                            String dateUpdateFormatted = simpleDateFormat.format(date);
                            tvLastUpdate.setText("Last update: " + dateUpdateFormatted);

                            JSONArray jsonArrayWeather = jsonObject.getJSONArray("weather");
                            JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                            String weatherStatus = jsonObjectWeather.getString("main");
                            tvWeatherStatus.setText(weatherStatus);
                            String icon = jsonObjectWeather.getString("icon");
                            Picasso.with(MainActivity.this).load("http://openweathermap.org/img/w/"+icon+".png").into(imgWeatherIcon);

                            JSONObject jsonObjectMain = jsonObject.getJSONObject("main");
                            String temp = jsonObjectMain.getString("temp");
                            String humidity = jsonObjectMain.getString("humidity");
                            Double d = Double.valueOf(temp);
                            String tempFormatted = String.valueOf(d.intValue());
                            tvTemp.setText(tempFormatted+"°C");
                            tvHumidity.setText(humidity+"%");

                            JSONObject jsonObjectWind = jsonObject.getJSONObject("wind");
                            String wind = jsonObjectWind.getString("speed");
                            tvWind.setText(wind+"m/s");

                            JSONObject jsonObjectCloud = jsonObject.getJSONObject("clouds");
                            String cloud = jsonObjectCloud.getString("all");
                            tvCloud.setText(cloud+"%");

                            JSONObject jsonObjectSys = jsonObject.getJSONObject("sys");
                            String country = jsonObjectSys.getString("country");
                            tvCountryName.setText("Country: " + country);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(stringRequest); // SAME AS PREVIOUS, POPULATE JSON
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        launchLocation();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Assume thisActivity is the current activity
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // If you get permission, launch the camera
                    launchLocation();
                } else {
                    // If you do not get permission, show a Toast
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

}
