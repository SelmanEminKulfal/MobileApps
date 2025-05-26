package com.example.sqliteapp;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.content.ContentValues;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    // Butonlar
    Button buttonKayit, buttonGoster, buttonSil, buttonGuncelle;

    // EditText alanları
    EditText editTextAd, editTextSoyad, editTextYas, editTextSehir, editTextId;

    // TextView
    TextView textViewBilgiler;

    // Veritabanı nesnesi
    private Veritabani v1;

    // Veritabanında kullanılacak sütunlar
    private String[] sutunlar = {"ad", "soyad", "yas", "sehir"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // XML'deki bileşenlerle eşleştirme
        buttonKayit = findViewById(R.id.buttonKayit);
        buttonGoster = findViewById(R.id.buttonGoster);
        buttonSil = findViewById(R.id.buttonSil);
        buttonGuncelle = findViewById(R.id.buttonGuncelle);

        editTextAd = findViewById(R.id.editTextAd);
        editTextSoyad = findViewById(R.id.editTextSoyad);
        editTextYas = findViewById(R.id.editTextYas);
        editTextSehir = findViewById(R.id.editTextSehir);
        editTextId = findViewById(R.id.editTextId);

        textViewBilgiler = findViewById(R.id.textViewBilgiler);

        v1 = Veritabani(this);

        // Kayıt Ekle
        buttonKayit.setOnClickListener(view -> {
            Log.d(TAG, "Kayıt Ekle butonuna basıldı.");
            KayitEkle(
                    editTextAd.getText().toString(),
                    editTextSoyad.getText().toString(),
                    editTextYas.getText().toString(),
                    editTextSehir.getText().toString()
            );
        });

        // Kayıtları Göster
        buttonGoster.setOnClickListener(view -> {
            Log.d(TAG, "Kayıt Göster butonuna basıldı.");
            KayitGoster(KayitGetir());
        });

        // Kayıt Sil
        buttonSil.setOnClickListener(view -> {
            Log.d(TAG, "Kayıt Sil butonuna basıldı.");
            KayitSil(editTextAd.getText().toString());
        });

        // Kayıt Güncelle
        buttonGuncelle.setOnClickListener(view -> {
            Log.d(TAG, "Kayıt Güncelle butonuna basıldı.");
            KayitGuncelle(
                    editTextAd.getText().toString(),
                    editTextSoyad.getText().toString(),
                    editTextYas.getText().toString(),
                    editTextSehir.getText().toString()
            );
        });
    }

    // Veritabanından kayıtları çeker
    private Cursor KayitGetir() {
        SQLiteDatabase db = v1.getWritableDatabase();
        return db.query("OgrenciBilgi", sutunlar, null, null, null, null, null);
    }

    // Kayıtları TextView'e yazar
    private void KayitGoster(Cursor goster) {
        StringBuilder builder = new StringBuilder();
        try {
            while (goster.moveToNext()) {
                int colAd = goster.getColumnIndexOrThrow("ad");
                int colSoyad = goster.getColumnIndexOrThrow("soyad");
                int colYas = goster.getColumnIndexOrThrow("yas");
                int colSehir = goster.getColumnIndexOrThrow("sehir");

                String ad = goster.getString(colAd);
                String soyad = goster.getString(colSoyad);
                int yas = goster.getInt(colYas);
                String sehir = goster.getString(colSehir);

                builder.append("Ad: ").append(ad)
                        .append(", Soyad: ").append(soyad)
                        .append(", Yaş: ").append(yas)
                        .append(", Şehir: ").append(sehir)
                        .append("\n");
            }
            textViewBilgiler.setText(builder.toString());
        } catch (Exception e) {
            Log.d(TAG, "Kayıt gösterilirken hata oluştu: " + e.getMessage());
        } finally {
            goster.close();
        }
    }

    // Kayıt ekleme
    private void KayitEkle(String ad, String soyad, String yas, String sehir) {
        SQLiteDatabase db = v1.getWritableDatabase();
        ContentValues veriler = new ContentValues();
        veriler.put("ad", ad);
        veriler.put("soyad", soyad);
        veriler.put("yas", Integer.parseInt(yas));
        veriler.put("sehir", sehir);
        try {
            db.insertOrThrow("OgrenciBilgi", null, veriler);
            Log.d(TAG, "Kayıt başarıyla eklendi.");
        } catch (Exception e) {
            Log.d(TAG, "Kayıt eklenirken hata: " + e.getMessage());
        }
    }

    // Kayıt silme
    private void KayitSil(String ad) {
        SQLiteDatabase db = v1.getWritableDatabase();
        try {
            db.delete("OgrenciBilgi", "ad=?", new String[]{ad});
            db.close();
            Log.d(TAG, "Kayıt silindi.");
        } catch (Exception e) {
            Log.d(TAG, "Silme hatası: " + e.getMessage());
        }
    }

    // Kayıt güncelleme
    private void KayitGuncelle(String ad, String soyad, String yas, String sehir) {
        try {
            SQLiteDatabase db = v1.getWritableDatabase();
            ContentValues cvGuncelle = new ContentValues();
            cvGuncelle.put("soyad", soyad);
            cvGuncelle.put("yas", Integer.parseInt(yas));
            cvGuncelle.put("sehir", sehir);
            db.update("OgrenciBilgi", cvGuncelle, "ad=?", new String[]{ad});
            Log.d(TAG, "Kayıt güncellendi.");
        } catch (Exception e) {
            Log.d(TAG, "Güncelleme hatası: " + e.getMessage());
        }
    }
}


