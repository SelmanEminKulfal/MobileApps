package com.example.weatherappwithapi;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class WeatherViewModel extends ViewModel {
    private MutableLiveData<CurrentWeatherResponse> currentWeather = new MutableLiveData<>();
    private MutableLiveData<ForecastResponse> forecast = new MutableLiveData<>();
}

