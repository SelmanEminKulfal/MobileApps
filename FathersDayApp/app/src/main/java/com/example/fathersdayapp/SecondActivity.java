package com.example.fathersdayapp;

import android.media.MediaPlayer;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class SecondActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        // Medya oynatıcıyı başlat
        // R.raw.bana_bir_masal_anlat_baba, raw klasöründeki ses dosyanızın adı olmalı
        mediaPlayer = MediaPlayer.create(this, R.raw.bana_bir_masal_anlat_baba);
        mediaPlayer.setLooping(true); // Şarkıyı sürekli tekrar et
        mediaPlayer.start(); // Şarkıyı çalmaya başla
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Activity yok edildiğinde medya oynatıcıyı serbest bırak
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Uygulama arka plana geçtiğinde müziği duraklat
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Uygulama tekrar ön plana geldiğinde müziği devam ettir
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
    }
}