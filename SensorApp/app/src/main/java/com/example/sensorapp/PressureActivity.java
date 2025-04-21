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
public class PressureActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor pressureSensor;
    private TextView dataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_pressure.xml layout dosyasını bu aktivite ile ilişkilendirir
        setContentView(R.layout.activity_pressure);

        // Layout dosyasındaki TextView'i bul
        dataTextView = findViewById(R.id.text_pressure_data);

        // SensorManager sistem servisine erişim sağla
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Basınç sensörünü bul
        if (sensorManager != null) {
            pressureSensor = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
        }

        // Sensör bulunamadıysa kullanıcıya bilgi ver
        if (pressureSensor == null) {
            dataTextView.setText("Basınç sensörü bulunamadı.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity görünür olduğunda sensör dinleyicisini kaydet
        if (pressureSensor != null) {
            sensorManager.registerListener(this, pressureSensor, SensorManager.SENSOR_DELAY_NORMAL);
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
        if (event.sensor.getType() == Sensor.TYPE_PRESSURE) {
            // Basınç verisi genellikle tek bir float değeridir (hPa/mbar)
            float pressure = event.values[0]; // hPa veya mbar cinsinden

            // TextView'i güncelleyerek veriyi göster
            String sensorData = String.format("Basınç: %.2f hPa", pressure);
            dataTextView.setText(sensorData);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Sensör doğruluğu değiştiğinde burası çağrılır
    }
}