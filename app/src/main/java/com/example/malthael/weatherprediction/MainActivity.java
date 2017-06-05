package com.example.malthael.weatherprediction;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
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
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MainActivity extends AppCompatActivity {
    EditText edtCitySearch;
    Button btnSearch, btnNextDays;
    TextView tvCityName, tvCountryName, tvTemp, tvWeatherStatus, tvCloud, tvHumidity, tvWind, tvLastUpdate;
    ImageView imgWeatherIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        edtCitySearch = (EditText) findViewById(R.id.edt_city_input);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnNextDays = (Button) findViewById(R.id.btn_next_days);
        tvCityName = (TextView) findViewById(R.id.tv_city_name);
        tvCountryName = (TextView) findViewById(R.id.tv_country_name);
        tvTemp = (TextView) findViewById(R.id.tv_temp);
        tvWeatherStatus = (TextView) findViewById(R.id.tv_weather_status);
        tvCloud = (TextView) findViewById(R.id.tv_cloud);
        tvHumidity = (TextView) findViewById(R.id.tv_humidity);
        tvWind = (TextView) findViewById(R.id.tv_wind);
        tvLastUpdate = (TextView) findViewById(R.id.tv_last_update);
        imgWeatherIcon = (ImageView) findViewById(R.id.imgv_weather_icon);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = edtCitySearch.getText().toString();
                if(city.equals("")) {
                    Toast.makeText(MainActivity.this, "Please input a location.", Toast.LENGTH_SHORT).show();

                }else {
                    getCurrentWeatherData(city);
                }

            }
        });

        btnNextDays.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvCityName.equals("City:")) {
                    Toast.makeText(MainActivity.this, "Please input a location", Toast.LENGTH_SHORT).show();
                }else {
                    Intent intent = new Intent(MainActivity.this, CheckNextDays.class);
                    String city = edtCitySearch.getText().toString();
                    intent.putExtra("cityName", city);
                    startActivity(intent);
                }
            }
        });
    }

    public void getCurrentWeatherData(String data) {
        RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
        String url = "http://api.openweathermap.org/data/2.5/weather?q="+data+"&units=metric&appid=f9c2ac0eda679b17d06683868e251e1d";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
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
        requestQueue.add(stringRequest);
    }
}
