package com.example.weatherapp;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private EditText editTextCity;
    private Button btnGetWeather;
    private TextView textViewTemperature, textViewWeather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI bileşenlerini tanımla
        editTextCity = findViewById(R.id.editTextCity);
        btnGetWeather = findViewById(R.id.btnGetWeather);
        textViewTemperature = findViewById(R.id.textViewTemperature);
        textViewWeather = findViewById(R.id.textViewWeather);

        // Butona tıklama olayı
        btnGetWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String city = editTextCity.getText().toString();
                if (!city.isEmpty()) {
                    getWeather(city);
                }
            }
        });
    }

    // Hava durumu verilerini al ve UI'ya yansıt
    private void getWeather(String city) {
        WeatherService weatherService = RetrofitClient.getRetrofitInstance().create(WeatherService.class);
        Call<WeatherResponse> call = weatherService.getWeather(city, "524c1023a86e0d2228a9e22dfbef1d60", "metric", "tr");

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Verileri UI'ya yansıt
                    textViewTemperature.setText("Sıcaklık: " + response.body().getMain().getTemp() + "°C");
                    textViewWeather.setText("Hava Durumu: " + response.body().getWeather().get(0).getDescription());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                textViewTemperature.setText("Hata: " + t.getMessage());
                textViewWeather.setText("");
            }
        });
    }
}
