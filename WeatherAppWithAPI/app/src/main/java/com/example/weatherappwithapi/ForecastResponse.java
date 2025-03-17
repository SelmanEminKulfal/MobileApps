package com.example.weatherappwithapi;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ForecastResponse {
    @SerializedName("list")
    private List<ForecastItem> list;

    public List<ForecastItem> getList() {
        return list;
    }

    public static class ForecastItem {
        @SerializedName("dt")
        private long dt;
        @SerializedName("main")
        private Main main;
        @SerializedName("weather")
        private List<CurrentWeatherResponse.Weather> weather;

        public long getDt() {
            return dt;
        }

        public Main getMain() {
            return main;
        }

        public List<CurrentWeatherResponse.Weather> getWeather() {
            return weather;
        }
    }
}