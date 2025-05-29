package com.example.weatherapp.service; // Paket adınızla eşleşmeli

import com.example.weatherapp.model.WeatherResponse; // Yeni modeliniz
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    @GET("current.json") // WeatherAPI.com güncel hava durumu endpoint'i
    Call<WeatherResponse> getCurrentWeatherData(
            @Query("key") String apiKey,
            @Query("q") String query, // Şehir adı veya koordinatlar
            @Query("lang") String lang // Dil (örn: "tr")
    );
}