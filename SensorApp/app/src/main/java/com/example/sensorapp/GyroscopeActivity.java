package com.example.sensorapp; // Burayı kendi paket adınızla değiştirin

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

// SensorEventListener arayüzünü implement ediyoruz
public class GyroscopeActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;
    private TextView dataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_gyroscope.xml layout dosyasını bu aktivite ile ilişkilendirir
        setContentView(R.layout.activity_gyroscope);

        // Layout dosyasındaki TextView'i bul
        dataTextView = findViewById(R.id.text_gyroscope_data);

        // SensorManager sistem servisine erişim sağla
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Jiroskop sensörünü bul
        if (sensorManager != null) {
            gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        }

        // Sensör bulunamadıysa kullanıcıya bilgi ver
        if (gyroscopeSensor == null) {
            dataTextView.setText("Jiroskop sensörü bulunamadı.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity görünür olduğunda sensör dinleyicisini kaydet
        if (gyroscopeSensor != null) {
            sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);
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
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // Jiroskop verileri 3 boyuttur (X, Y, Z eksenleri etrafındaki dönüş hızı)
            float x = event.values[0]; // rad/s
            float y = event.values[1]; // rad/s
            float z = event.values[2]; // rad/s

            // TextView'i güncelleyerek verileri göster
            String sensorData = String.format("Jiroskop (rad/s):\nX: %.2f\nY: %.2f\nZ: %.2f", x, y, z);
            dataTextView.setText(sensorData);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Sensör doğruluğu değiştiğinde burası çağrılır
    }
}