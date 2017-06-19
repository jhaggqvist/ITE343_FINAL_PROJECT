package com.example.malthael.weatherprediction;

import android.content.Intent;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

public class CheckNextDays extends AppCompatActivity {

    ImageButton imgBtnBack;
    TextView tvCityName;
    ListView lvForecast;
    CustomAdapter customAdapter;
    ArrayList<WeatherForecast> arrayWeatherForecast;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_next_days);

        getReferences();

        Intent intent = getIntent();
        String city = intent.getStringExtra("cityName");

        forcast8DaysToCome(city);

        imgBtnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void getReferences() {
        imgBtnBack = (ImageButton) findViewById(R.id.imgbtn_back);
        tvCityName = (TextView) findViewById(R.id.tv_city_name);
        lvForecast = (ListView) findViewById(R.id.lv_forecast);
        arrayWeatherForecast = new ArrayList<WeatherForecast>();
        customAdapter = new CustomAdapter(CheckNextDays.this, arrayWeatherForecast);
        lvForecast.setAdapter(customAdapter);
    }

    public void forcast8DaysToCome(String data) {
        RequestQueue requestQueue = Volley.newRequestQueue(CheckNextDays.this);
        String url = "http://api.openweathermap.org/data/2.5/forecast/daily?q="+data+"&units=metric&cnt=8&appid=f9c2ac0eda679b17d06683868e251e1d";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            JSONObject jsonObjectCity = jsonObject.getJSONObject("city");
                            String name = jsonObjectCity.getString("name");
                            tvCityName.setText(name);

                            JSONArray jsonArrayList = jsonObject.getJSONArray("list");
                            for (int i = 0; i < jsonArrayList.length(); i++) {
                                JSONObject jsonObjectList = jsonArrayList.getJSONObject(i);
                                String date = jsonObjectList.getString("dt");
                                long l = Long.valueOf(date);
                                Date dateInMs = new Date(l*1000L);
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE yyyy-MM-dd");
                                String dateFormatted = simpleDateFormat.format(dateInMs);

                                JSONObject jsonObjectTemp = jsonObjectList.getJSONObject("temp");
                                String max = jsonObjectTemp.getString("max");
                                String min = jsonObjectTemp.getString("min");
                                Double d = Double.valueOf(max);
                                Double d2 = Double.valueOf(min);
                                String TempMax = String.valueOf(d.intValue());
                                String TempMin = String.valueOf(d2.intValue());

                                JSONArray jsonArrayWeather = jsonObjectList.getJSONArray("weather");
                                JSONObject jsonObjectWeather = jsonArrayWeather.getJSONObject(0);
                                String status = jsonObjectWeather.getString("description");
                                String icon = jsonObjectWeather.getString("icon");

                                arrayWeatherForecast.add(new WeatherForecast(dateFormatted, status, icon, TempMax, TempMin));
                            }
                            customAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        requestQueue.add(stringRequest);
    }
}
