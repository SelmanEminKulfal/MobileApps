package com.example.sensorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

// SensorEventListener arayüzünü implement ediyoruz
public class ThermometerActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor thermometerSensor;
    private TextView dataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_thermometer.xml layout dosyasını bu aktivite ile ilişkilendirir
        setContentView(R.layout.activity_thermometer);

        // Layout dosyasındaki TextView'i bul
        dataTextView = findViewById(R.id.text_thermometer_data);

        // SensorManager sistem servisine erişim sağla
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Ortam Sıcaklığı sensörünü bul
        if (sensorManager != null) {
            thermometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_AMBIENT_TEMPERATURE);
            // Not: TYPE_TEMPERATURE artık önerilmiyor (deprecated).
            // Eski cihazlar için TYPE_TEMPERATURE kullanabilirsiniz:
            // thermometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_TEMPERATURE);
        }


        // Sensör bulunamadıysa kullanıcıya bilgi ver
        if (thermometerSensor == null) {
            dataTextView.setText("Ortam Sıcaklığı sensörü bulunamadı.\n(Bu sensör modern cihazlarda nadirdir)");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity görünür olduğunda sensör dinleyicisini kaydet
        if (thermometerSensor != null) {
            sensorManager.registerListener(this, thermometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Activity görünmez olduğunda sensör dinleyicisini kaydı sil
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Sensör değerleri değiştiğinde burası çağrılır
        // Hem TYPE_AMBIENT_TEMPERATURE hem de TYPE_TEMPERATURE için
        // ilk değer genellikle sıcaklıktır.
        if (event.sensor.getType() == Sensor.TYPE_AMBIENT_TEMPERATURE ||
                event.sensor.getType() == Sensor.TYPE_TEMPERATURE) { // Eski cihazlar için kontrol
            // Sıcaklık verisi tek bir float değeridir (Celsius)
            float temperature = event.values[0]; // Celsius cinsinden

            // TextView'i güncelleyerek veriyi göster
            String sensorData = String.format("Sıcaklık: %.2f °C", temperature);
            dataTextView.setText(sensorData);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Sensör doğruluğu değiştiğinde burası çağrılır
    }
}