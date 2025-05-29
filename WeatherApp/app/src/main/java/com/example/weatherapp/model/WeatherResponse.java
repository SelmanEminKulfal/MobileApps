package com.example.weatherapp.model;

import com.google.gson.annotations.SerializedName;

public class WeatherResponse {
    @SerializedName("location")
    private Location location;

    @SerializedName("current")
    private Current current;

    // Getter methods
    public Location getLocation() {
        return location;
    }

    public Current getCurrent() {
        return current;
    }

    // Setter methods (Retrofit ve Gson için genellikle zorunlu değildir, ama eklenebilir)
    public void setLocation(Location location) {
        this.location = location;
    }

    public void setCurrent(Current current) {
        this.current = current;
    }
}
