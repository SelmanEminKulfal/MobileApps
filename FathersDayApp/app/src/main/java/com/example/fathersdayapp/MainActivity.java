package com.example.fathersdayapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private Button btnCheckDate;
    private TextView tvErrorMessage;
    private Button btnGoBack;

    // Hedef tarih: 4 Kasım 1975
    private static final int TARGET_DAY = 4;
    private static final int TARGET_MONTH = Calendar.NOVEMBER; // Calendar.NOVEMBER = 10 (0'dan başlar)
    private static final int TARGET_YEAR = 1975;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Locale locale = new Locale("tr");
        Locale.setDefault(locale);
        Configuration config = getResources().getConfiguration();
        config.setLocale(locale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            createConfigurationContext(config);
        } else {
            getResources().updateConfiguration(config, getResources().getDisplayMetrics());
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // XML elemanlarını Java koduna bağlama
        datePicker = findViewById(R.id.datePicker);
        btnCheckDate = findViewById(R.id.btnCheckDate);
        tvErrorMessage = findViewById(R.id.tvErrorMessage);
        btnGoBack = findViewById(R.id.btnGoBack);

        // DatePicker'ın başlangıç tarihini ayarlayabiliriz (isteğe bağlı)
        // Örneğin, 1 Ocak 2000 olarak ayarlayalım:
        // datePicker.init(2000, 0, 1, null); // Yıl, Ay (0-11), Gün

        btnCheckDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Seçilen tarihi al
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth(); // Ay 0'dan başlar (Ocak=0, Kasım=10)
                int year = datePicker.getYear();

                // Takvim nesnesini kullanarak hedef tarihi oluştur
                Calendar targetDate = Calendar.getInstance();
                targetDate.set(TARGET_YEAR, TARGET_MONTH, TARGET_DAY);

                // Kullanıcının seçtiği tarihi Takvim nesnesine dönüştür
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, day);

                // Tarihleri karşılaştır
                if (selectedDate.get(Calendar.YEAR) == targetDate.get(Calendar.YEAR) &&
                        selectedDate.get(Calendar.MONTH) == targetDate.get(Calendar.MONTH) &&
                        selectedDate.get(Calendar.DAY_OF_MONTH) == targetDate.get(Calendar.DAY_OF_MONTH)) {
                    // Tarih doğru ise ikinci ekrana geç
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    startActivity(intent);
                    // Ana ekranı kapatabiliriz, böylece geri tuşuna basıldığında tekrar buraya dönülmez
                    finish();
                } else {
                    // Tarih yanlış ise hata mesajını göster ve "Ana Sayfaya Dön" butonunu görünür yap
                    tvErrorMessage.setVisibility(View.VISIBLE);
                    btnGoBack.setVisibility(View.VISIBLE);
                }
            }
        });

        btnGoBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Hata mesajını ve geri dön butonunu gizle
                tvErrorMessage.setVisibility(View.GONE);
                btnGoBack.setVisibility(View.GONE);
                // DatePicker'ı başlangıç durumuna sıfırlayabilirsiniz (isteğe bağlı)
                // datePicker.init(2000, 0, 1, null);
            }
        });
    }
}