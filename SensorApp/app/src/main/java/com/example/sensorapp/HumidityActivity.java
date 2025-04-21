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
public class HumidityActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor humiditySensor;
    private TextView dataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_humidity.xml layout dosyasını bu aktivite ile ilişkilendirir
        setContentView(R.layout.activity_humidity);

        // Layout dosyasındaki TextView'i bul
        dataTextView = findViewById(R.id.text_humidity_data);

        // SensorManager sistem servisine erişim sağla
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Nem sensörünü bul
        if (sensorManager != null) {
            humiditySensor = sensorManager.getDefaultSensor(Sensor.TYPE_RELATIVE_HUMIDITY);
        }

        // Sensör bulunamadıysa kullanıcıya bilgi ver
        if (humiditySensor == null) {
            dataTextView.setText("Nem sensörü bulunamadı.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity görünür olduğunda sensör dinleyicisini kaydet
        if (humiditySensor != null) {
            sensorManager.registerListener(this, humiditySensor, SensorManager.SENSOR_DELAY_NORMAL);
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
        if (event.sensor.getType() == Sensor.TYPE_RELATIVE_HUMIDITY) {
            // Nem verisi genellikle tek bir float değeridir (%)
            float humidity = event.values[0]; // % cinsinden bağıl nem

            // TextView'i güncelleyerek veriyi göster
            String sensorData = String.format("Bağıl Nem: %.2f %%", humidity);
            dataTextView.setText(sensorData);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Sensör doğruluğu değiştiğinde burası çağrılır
    }
}