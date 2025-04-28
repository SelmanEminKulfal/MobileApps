package com.example.weatherapp.models;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class WeatherResponse {

    @SerializedName("weather") // JSON'daki "weather" alanına karşılık gelir
    private List<Weather> weather; // Birden fazla hava durumu açıklaması olabilir, o yüzden Liste

    @SerializedName("main") // JSON'daki "main" alanına karşılık gelir
    private Main main; // Ana hava durumu bilgileri (sıcaklık, nem vb.)

    @SerializedName("name") // JSON'daki "name" alanına karşılık gelir
    private String name; // Şehir adı

    // Getter metodları (Verilere erişmek için)
    public List<Weather> getWeather() {
        return weather;
    }

    public Main getMain() {
        return main;
    }

    public String getName() {
        return name;
    }
}
