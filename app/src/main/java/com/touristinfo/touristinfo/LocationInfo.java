package com.touristinfo.touristinfo;

/**
 * Created by vladimir on 2/4/2016.
 */
public class LocationInfo {
    public String name = "";
    public String description = "";
    public double latitude = 0.0;
    public double longitude = 0.0;

    LocationInfo() {}
    LocationInfo(String name, String description, double latitude, double longitude) {
        this.name = name;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
