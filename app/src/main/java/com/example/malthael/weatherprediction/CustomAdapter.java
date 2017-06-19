package com.example.malthael.weatherprediction;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Malthael on 6/19/2017.
 */

public class CustomAdapter extends BaseAdapter {
    Context context;
    ArrayList<WeatherForecast> arrayList;

    public CustomAdapter(Context context, ArrayList<WeatherForecast> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrayList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.listview_row, null);

        WeatherForecast weatherForecast = arrayList.get(position);
        TextView tvDay = (TextView) convertView.findViewById(R.id.tv_day);
        TextView tvStatus = (TextView) convertView.findViewById(R.id.tv_status);
        TextView tvMaxTemp = (TextView) convertView.findViewById(R.id.tv_maxtemp);
        TextView tvMinTemp = (TextView) convertView.findViewById(R.id.tv_mintemp);
        ImageView imgWeather = (ImageView) convertView.findViewById(R.id.imgv_weather_icon);

        tvDay.setText(weatherForecast.Day);
        tvStatus.setText(weatherForecast.Status);
        tvMaxTemp.setText(weatherForecast.MaxTemp+"°C");
        tvMinTemp.setText(weatherForecast.MinTemp+"°C");
        Picasso.with(context).load("http://openweathermap.org/img/w/"+weatherForecast.ImageWeather+".png").into(imgWeather);
        return convertView;
    }
}
