package com.example.weatherapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherService {

    @GET("data/2.5/weather")
    Call<WeatherResponse> getWeather(
            @Query("q") String city, // Şehir adı
            @Query("appid") String apiKey, // API Anahtarı
            @Query("units") String units, // Ölçü birimi
            @Query("lang") String lang // Dil
    );
}
