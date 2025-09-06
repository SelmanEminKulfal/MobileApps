package com.example.birthdayapp;

import androidx.appcompat.app.AppCompatActivity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.ImageView;

public class SecondActivity extends AppCompatActivity {

    private ImageView imageView;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // XML'deki ImageView bileşenini Java'ya bağlıyoruz.
        imageView = findViewById(R.id.imageView);

        // Ekranın arkaplanındaki görseli yüklüyoruz.
        // `annemin_fotografi` yerine kendi dosya adınızı yazın.
        imageView.setImageResource(R.drawable.poem);

        // MediaPlayer'ı başlatıp müziği çalmaya başlıyoruz.
        // `candan_ercetin_annem` yerine kendi müzik dosya adınızı yazın.
        mediaPlayer = MediaPlayer.create(this, R.raw.candan_ercetin_annem);
        mediaPlayer.start(); // Şarkıyı başlat.
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Uygulama görünürden çıktığında müziği durduruyoruz.
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release(); // Kaynakları serbest bırak.
            mediaPlayer = null;
        }
    }
}