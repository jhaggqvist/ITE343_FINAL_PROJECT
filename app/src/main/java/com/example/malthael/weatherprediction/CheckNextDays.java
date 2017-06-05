package com.example.malthael.weatherprediction;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class CheckNextDays extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_next_days);

        Intent intent = getIntent();
        String city = intent.getStringExtra("cityName");
    }
}
