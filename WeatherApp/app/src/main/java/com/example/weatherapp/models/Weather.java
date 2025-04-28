package com.example.weatherapp.models;

import com.google.gson.annotations.SerializedName;

public class Weather {

    @SerializedName("description") // JSON'daki "description" alanına karşılık gelir
    private String description; // Detaylı hava durumu açıklaması (örn: "clear sky")

    @SerializedName("icon") // JSON'daki "icon" alanına karşılık gelir
    private String icon; // Hava durumu ikonu kodu (örn: "01d")

    // Getter metodları
    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }
}