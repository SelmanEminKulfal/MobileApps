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
public class ProximityActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private TextView dataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_proximity.xml layout dosyasını bu aktivite ile ilişkilendirir
        setContentView(R.layout.activity_proximity);

        // Layout dosyasındaki TextView'i bul
        dataTextView = findViewById(R.id.text_proximity_data);

        // SensorManager sistem servisine erişim sağla
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Yakınlık sensörünü bul
        if (sensorManager != null) {
            proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        }

        // Sensör bulunamadıysa kullanıcıya bilgi ver
        if (proximitySensor == null) {
            dataTextView.setText("Yakınlık sensörü bulunamadı.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity görünür olduğunda sensör dinleyicisini kaydet
        if (proximitySensor != null) {
            sensorManager.registerListener(this, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
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
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            // Yakınlık verisi genellikle tek bir float değeridir (cm)
            // Çoğu sensör sadece 0 (yakın) veya max_range (uzak) değerini verir
            float distance = event.values[0]; // cm cinsinden uzaklık

            // TextView'i güncelleyerek veriyi göster
            String sensorData = String.format("Uzaklık: %.2f cm", distance);
            dataTextView.setText(sensorData);

            // Genellikle 'yakın' veya 'uzak' durumunu kontrol etmek daha yaygındır:
            // if (distance < proximitySensor.getMaximumRange()) { // Yakın
            //     dataTextView.setText("Durum: Yakın");
            // } else { // Uzak
            //     dataTextView.setText("Durum: Uzak");
            // }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Sensör doğruluğu değiştiğinde burası çağrılır
    }
}