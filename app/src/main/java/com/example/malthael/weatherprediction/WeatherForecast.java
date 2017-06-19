package com.example.malthael.weatherprediction;

/**
 * Created by Malthael on 6/19/2017.
 */

public class WeatherForecast {
    public String Day;
    public String Status;
    public String ImageWeather;
    public String MaxTemp;
    public String MinTemp;

    public WeatherForecast(String day, String status, String imageWeather, String maxTemp, String minTemp) {
        Day = day;
        Status = status;
        ImageWeather = imageWeather;
        MaxTemp = maxTemp;
        MinTemp = minTemp;
    }
}
