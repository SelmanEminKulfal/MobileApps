package com.example.weatherapp.models;

import com.google.gson.annotations.SerializedName;

public class Main {

    @SerializedName("temp") // JSON'daki "temp" alanına karşılık gelir
    private double temp; // Sıcaklık

    @SerializedName("feels_like") // JSON'daki "feels_like" alanına karşılık gelir
    private double feelsLike; // Hissedilen sıcaklık

    @SerializedName("temp_min") // JSON'daki "temp_min" alanına karşılık gelir
    private double tempMin; // Minimum sıcaklık

    @SerializedName("temp_max") // JSON'daki "temp_max" alanına karşılık gelir
    private double tempMax; // Maksimum sıcaklık

    @SerializedName("pressure") // JSON'daki "pressure" alanına karşılık gelir
    private int pressure; // Basınç

    @SerializedName("humidity") // JSON'daki "humidity" alanına karşılık gelir
    private int humidity; // Nem yüzdesi

    // Getter metodları
    public double getTemp() {
        return temp;
    }

    public double getFeelsLike() {
        return feelsLike;
    }

    public double getTempMin() {
        return tempMin;
    }

    public double getTempMax() {
        return tempMax;
    }

    public int getPressure() {
        return pressure;
    }

    public int getHumidity() {
        return humidity;
    }
}