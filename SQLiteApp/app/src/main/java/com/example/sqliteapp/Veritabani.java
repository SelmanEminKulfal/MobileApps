package com.example.uygulama;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class Veritabani extends SQLiteOpenHelper {

    // Veritabanı adı ve versiyonu
    private static final String VERITABANI_ADI = "Kisiler.db";
    private static final int VERITABANI_VERSIYONU = 1;

    // Tablo ve sütun adları
    private static final String TABLO_ADI = "OgrenciBilgi";
    private static final String SUTUN_ID = "id";
    private static final String SUTUN_AD = "ad";
    private static final String SUTUN_SOYAD = "soyad";
    private static final String SUTUN_YAS = "yas";
    private static final String SUTUN_SEHIR = "sehir";

    // SQL tablo oluşturma sorgusu
    private static final String SQL_OLUSTUR =
            "CREATE TABLE " + TABLO_ADI + " (" +
                    SUTUN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    SUTUN_AD + " TEXT NOT NULL, " +
                    SUTUN_SOYAD + " TEXT, " +
                    SUTUN_YAS + " INTEGER, " +
                    SUTUN_SEHIR + " TEXT);";

    // Constructor
    public Veritabani(Context context) {
        super(context, VERITABANI_ADI, null, VERITABANI_VERSIYONU);
    }

    // Veritabanı ilk oluşturulduğunda çağrılır
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_OLUSTUR);
        Log.d("Veritabani", "Veritabanı oluşturuldu.");
    }

    // Veritabanı versiyonu değiştiğinde çağrılır
    @Override
    public void onUpgrade(SQLiteDatabase db, int eskiVersiyon, int yeniVersiyon) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLO_ADI);
        onCreate(db);
        Log.d("Veritabani", "Veritabanı güncellendi.");
    }
}
