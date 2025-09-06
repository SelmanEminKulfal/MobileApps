package com.example.birthdayapp;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private DatePicker datePicker;
    private Button buttonCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        datePicker = findViewById(R.id.datePicker);
        buttonCheck = findViewById(R.id.buttonCheck);

        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Kullanıcının seçtiği tarihi alıyoruz.
                int day = datePicker.getDayOfMonth();
                int month = datePicker.getMonth() + 1;
                int year = datePicker.getYear();

                // Doğru doğum tarihi için koşulu kontrol ediyoruz.
                if (day == 5 && month == 9 && year == 1979) {
                    // Tarih doğruysa, ikinci ekrana geçiş yapacak Intent'i başlatıyoruz.
                    Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                    startActivity(intent);
                } else {
                    // Tarih yanlışsa, kullanıcıya bir uyarı gösteriyoruz.
                    Toast.makeText(MainActivity.this, "Üzgünüm, yanlış tarih girdiniz. Lütfen tekrar deneyin.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}