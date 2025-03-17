package com.example.weatherappwithapi;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private WeatherViewModel weatherViewModel;
    private TextView locationTextView;
    private TextView temperatureTextView;
    private TextView descriptionTextView;
    private ImageView weatherIconImageView;
    private TextView feelsLikeTextView;
    private TextView humidityTextView;
    private TextView windSpeedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.application_screen);

        locationTextView = findViewById(R.id.locationTextView);
        temperatureTextView = findViewById(R.id.temperatureTextView);
        descriptionTextView = findViewById(R.id.descriptionTextView);
        weatherIconImageView = findViewById(R.id.weatherIconImageView);
        feelsLikeTextView = findViewById(R.id.feelsLikeTextView);
        humidityTextView = findViewById(R.id.humidityTextView);
        windSpeedTextView = findViewById(R.id.windSpeedTextView);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        weatherViewModel = new ViewModelProvider(this).get(WeatherViewModel.class);

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            getLastLocation();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
            } else {
                Toast.makeText(this, "Konum izni reddedildi.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            fetchWeatherData(latitude, longitude);
                        } else {
                            Toast.makeText(MainActivity.this, "Konum alınamadı.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void fetchWeatherData(double latitude, double longitude) {
        String apiKey = "524c1023a86e0d2228a9e22dfbef1d60";
        WeatherApiService apiService = RetrofitClient.getApiService();

        Call<CurrentWeatherResponse> call = apiService.getCurrentWeather(latitude, longitude, apiKey, "metric");
        call.enqueue(new Callback<CurrentWeatherResponse>() {
            @Override
            public void onResponse(Call<CurrentWeatherResponse> call, Response<CurrentWeatherResponse> response) {
                if (response.isSuccessful()) {
                    CurrentWeatherResponse currentWeather = response.body();
                    if (currentWeather != null) {
                        updateUI(currentWeather);
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Hava durumu bilgisi alınamadı.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CurrentWeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Hava durumu bilgisi alınamadı.", Toast.LENGTH_SHORT).show();
                Log.e("MainActivity", "API Error", t);
            }
        });
    }

    private void updateUI(CurrentWeatherResponse currentWeather) {
        locationTextView.setText(currentWeather.getName());
        temperatureTextView.setText(String.format("%.1f°C", currentWeather.getMain().getTemp()));
        descriptionTextView.setText(currentWeather.getWeather().get(0).getDescription());
        feelsLikeTextView.setText(String.format("Hissedilen: %.1f°C", currentWeather.getMain().getFeelsLike()));
        humidityTextView.setText(String.format("Nem: %d%%", currentWeather.getMain().getHumidity()));
        windSpeedTextView.setText(String.format("Rüzgar: %.1f m/s", currentWeather.getWind().getSpeed()));

        String iconCode = currentWeather.getWeather().get(0).getIcon();
        String iconUrl = "https://openweathermap.org/img/w/" + iconCode + ".png";
        Glide.with(this).load(iconUrl).into(weatherIconImageView);
    }
}
