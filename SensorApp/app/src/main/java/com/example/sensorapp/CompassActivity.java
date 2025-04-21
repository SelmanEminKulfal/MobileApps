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
public class CompassActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    // Pusula genellikle Manyetik Alan sensörünü kullanır
    private Sensor magneticFieldSensor;
    private TextView dataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_compass.xml layout dosyasını bu aktivite ile ilişkilendirir
        setContentView(R.layout.activity_compass);

        // Layout dosyasındaki TextView'i bul
        dataTextView = findViewById(R.id.text_compass_data);

        // SensorManager sistem servisine erişim sağla
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Manyetik Alan (Pusula) sensörünü bul
        if (sensorManager != null) {
            magneticFieldSensor = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }

        // Sensör bulunamadıysa kullanıcıya bilgi ver
        if (magneticFieldSensor == null) {
            dataTextView.setText("Manyetik Alan (Pusula) sensörü bulunamadı.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity görünür olduğunda sensör dinleyicisini kaydet
        if (magneticFieldSensor != null) {
            sensorManager.registerListener(this, magneticFieldSensor, SensorManager.SENSOR_DELAY_NORMAL);
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
        if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            // Manyetik alan verileri 3 boyuttur (X, Y, Z)
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // TextView'i güncelleyerek verileri göster (mikroTesla - µT cinsinden)
            String sensorData = String.format("Manyetik Alan (µT):\nX: %.2f\nY: %.2f\nZ: %.2f", x, y, z);
            dataTextView.setText(sensorData);

            // Not: Gerçek pusula yönünü hesaplamak için ivmeölçer verileri de gerekir.
            // Bu kod sadece manyetik alanın ham değerlerini gösterir.
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Sensör doğruluğu değiştiğinde burası çağrılır
    }
}