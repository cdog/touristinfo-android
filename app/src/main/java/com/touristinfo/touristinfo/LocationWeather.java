package com.touristinfo.touristinfo;

import android.graphics.Bitmap;

import java.util.Date;

/**
 * Created by vladimir on 2/3/2016.
 */
public class LocationWeather {
    public String date = "Date: N/A";
    public String description = "Description: N/A";
    public String dayTemperature = "N/A";
    public String nightTemperature = "N/A";
    public String iconURL = null;
    public Bitmap bitmap = null;

    LocationWeather() {

    }

    LocationWeather(String date, String description, String dayTemperature, String nightTemperature, String iconURL) {
        this.date = date;
        this.description = description;
        this.dayTemperature = dayTemperature;
        this.nightTemperature = nightTemperature;
        this.iconURL = iconURL;
    }
}
