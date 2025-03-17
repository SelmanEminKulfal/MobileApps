package com.example.weatherappwithapi;

import com.google.gson.annotations.SerializedName;

public class Wind {
    @SerializedName("speed")
    private double speed;
    @SerializedName("deg")
    private int deg;

    public double getSpeed() {
        return speed;
    }

    public int getDeg() {
        return deg;
    }
}