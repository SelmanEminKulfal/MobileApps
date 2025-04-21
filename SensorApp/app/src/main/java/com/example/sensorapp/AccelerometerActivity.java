package com.example.sensorapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context; // Context sınıfını kullanmak için
import android.hardware.Sensor; // Sensor sınıfını kullanmak için
import android.hardware.SensorEvent; // Sensör olaylarını yakalamak için
import android.hardware.SensorEventListener; // Sensör olaylarını dinlemek için
import android.hardware.SensorManager; // Sensör servisine erişmek için
import android.os.Bundle;
import android.widget.TextView; // Sensör verisini göstermek için

// SensorEventListener arayüzünü implement ediyoruz
public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometerSensor;
    private TextView dataTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_accelerometer.xml layout dosyasını bu aktivite ile ilişkilendirir
        setContentView(R.layout.activity_accelerometer);

        // Layout dosyasındaki TextView'i bul
        dataTextView = findViewById(R.id.text_accelerometer_data);

        // SensorManager sistem servisine erişim sağla
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // İvmeölçer sensörünü bul
        // Cihazda bu sensör yoksa null dönebilir
        if (sensorManager != null) {
            accelerometerSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        }

        // Sensör bulunamadıysa kullanıcıya bilgi ver
        if (accelerometerSensor == null) {
            dataTextView.setText("İvmeölçer sensörü bulunamadı.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity görünür olduğunda sensör dinleyicisini kaydet
        // UI thread'ini bloklamamak için SENSOR_DELAY_NORMAL kullanıyoruz
        if (accelerometerSensor != null) {
            sensorManager.registerListener(this, accelerometerSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Activity görünmez olduğunda sensör dinleyicisini kaydı sil
        // Bu pil tasarrufu için önemlidir
        if (sensorManager != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        // Sensör değerleri değiştiğinde burası çağrılır
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // İvmeölçer verileri genellikle 3 boyuttur (X, Y, Z)
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            // TextView'i güncelleyerek verileri göster
            String sensorData = String.format("X: %.2f\nY: %.2f\nZ: %.2f", x, y, z);
            dataTextView.setText(sensorData);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Sensör doğruluğu değiştiğinde burası çağrılır
        // Şimdilik bu metodu boş bırakabiliriz
    }
}