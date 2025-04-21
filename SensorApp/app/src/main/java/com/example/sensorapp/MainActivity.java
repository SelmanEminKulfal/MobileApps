package com.example.sensorapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent; // Intent sınıfını kullanmak için
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.util.Log; // Log mesajları için (isteğe bağlı)

public class MainActivity extends AppCompatActivity {

    // Buton referansları
    private Button buttonAccelerometer;
    private Button buttonCompass;
    private Button buttonGyroscope;
    private Button buttonHumidity;
    private Button buttonLight;
    private Button buttonMagnometer;
    private Button buttonPressure;
    private Button buttonProximity;
    private Button buttonThermometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_main.xml layout dosyasını bu aktivite ile ilişkilendirir
        setContentView(R.layout.activity_main);

        // activity_main.xml dosyasındaki butonları ID'lerine göre bul
        buttonAccelerometer = findViewById(R.id.button_accelerometer);
        buttonCompass = findViewById(R.id.button_compass);
        buttonGyroscope = findViewById(R.id.button_gyroscope);
        buttonHumidity = findViewById(R.id.button_humidity);
        buttonLight = findViewById(R.id.button_light);
        buttonMagnometer = findViewById(R.id.button_magnometer);
        buttonPressure = findViewById(R.id.button_pressure);
        buttonProximity = findViewById(R.id.button_proximity);
        buttonThermometer = findViewById(R.id.button_thermometer);

        // Her buton için tıklama dinleyicisi (OnClickListener) ayarla
        buttonAccelerometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent oluştur: Bulunduğumuz Activity (MainActivity.this)
                // ve başlatmak istediğimiz Activity (AccelerometerActivity.class)
                Intent intent = new Intent(MainActivity.this, AccelerometerActivity.class);
                // Intent'i başlat (yeni Activity'yi aç)
                startActivity(intent);
            }
        });

        buttonCompass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent oluştur: Bulunduğumuz Activity (MainActivity.this)
                // ve başlatmak istediğimiz Activity (AccelerometerActivity.class)
                Intent intent = new Intent(MainActivity.this, CompassActivity.class);
                // Intent'i başlat (yeni Activity'yi aç)
                startActivity(intent);
            }
        });

        buttonGyroscope.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent oluştur: Bulunduğumuz Activity (MainActivity.this)
                // ve başlatmak istediğimiz Activity (AccelerometerActivity.class)
                Intent intent = new Intent(MainActivity.this, GyroscopeActivity.class);
                // Intent'i başlat (yeni Activity'yi aç)
                startActivity(intent);
            }
        });

        buttonHumidity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent oluştur: Bulunduğumuz Activity (MainActivity.this)
                // ve başlatmak istediğimiz Activity (AccelerometerActivity.class)
                Intent intent = new Intent(MainActivity.this, HumidityActivity.class);
                // Intent'i başlat (yeni Activity'yi aç)
                startActivity(intent);
            }
        });

        buttonLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent oluştur: Bulunduğumuz Activity (MainActivity.this)
                // ve başlatmak istediğimiz Activity (AccelerometerActivity.class)
                Intent intent = new Intent(MainActivity.this, LightActivity.class);
                // Intent'i başlat (yeni Activity'yi aç)
                startActivity(intent);
            }
        });

        buttonMagnometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent oluştur: Bulunduğumuz Activity (MainActivity.this)
                // ve başlatmak istediğimiz Activity (AccelerometerActivity.class)
                Intent intent = new Intent(MainActivity.this, MagnometerActivity.class);
                // Intent'i başlat (yeni Activity'yi aç)
                startActivity(intent);
            }
        });

        buttonPressure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent oluştur: Bulunduğumuz Activity (MainActivity.this)
                // ve başlatmak istediğimiz Activity (AccelerometerActivity.class)
                Intent intent = new Intent(MainActivity.this, PressureActivity.class);
                // Intent'i başlat (yeni Activity'yi aç)
                startActivity(intent);
            }
        });

        buttonProximity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent oluştur: Bulunduğumuz Activity (MainActivity.this)
                // ve başlatmak istediğimiz Activity (AccelerometerActivity.class)
                Intent intent = new Intent(MainActivity.this, ProximityActivity.class);
                // Intent'i başlat (yeni Activity'yi aç)
                startActivity(intent);
            }
        });

        buttonThermometer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Intent oluştur: Bulunduğumuz Activity (MainActivity.this)
                // ve başlatmak istediğimiz Activity (AccelerometerActivity.class)
                Intent intent = new Intent(MainActivity.this, ThermometerActivity.class);
                // Intent'i başlat (yeni Activity'yi aç)
                startActivity(intent);
            }
        });

    }
}