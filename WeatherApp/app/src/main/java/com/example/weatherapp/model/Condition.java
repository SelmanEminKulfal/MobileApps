package com.example.weatherapp.model;

import com.google.gson.annotations.SerializedName;

public class Condition {
    @SerializedName("text")
    private String text;

    @SerializedName("icon")
    private String icon; // İkon URL'si (örneğin: "//cdn.weatherapi.com/weather/64x64/day/113.png")

    @SerializedName("code")
    private int code;

    // Getter methods
    public String getText() {
        return text;
    }

    public String getIcon() {
        return icon;
    }

    public int getCode() {
        return code;
    }

    // Setter methods (isteğe bağlı)
    public void setText(String text) {
        this.text = text;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public void setCode(int code) {
        this.code = code;
    }
}