package com.example.weatherapp;
import com.example.weatherapp.models.WeatherResponse;
import com.example.weatherapp.models.Weather;
import com.example.weatherapp.models.Main;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Spinner; // Spinner sınıfını import edin
import android.widget.ArrayAdapter; // ArrayAdapter sınıfını import edin
import android.widget.AdapterView; // AdapterView ve OnItemSelectedListener için import edin
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView; // ImageView sınıfını import edin
import com.bumptech.glide.Glide; // Glide sınıfını import edin


import androidx.appcompat.app.AppCompatActivity;


import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

public class MainActivity extends AppCompatActivity {

    private ImageView weatherIconImageView; // ImageView değişkeni
    private static final String TAG = "WeatherApp";
    private TextView weatherInfoTextView;
    private Spinner citySpinner; // Spinner değişkeni

    private static final String API_KEY = "524c1023a86e0d2228a9e22dfbef1d60"; // <<=== BURAYI DEĞİŞTİRİN
    private RequestQueue queue; // RequestQueue'yu sınıf seviyesine taşıyalım

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherInfoTextView = findViewById(R.id.weatherInfoTextView);
        citySpinner = findViewById(R.id.citySpinner);
        weatherIconImageView = findViewById(R.id.weatherIconImageView);

        // Volley RequestQueue oluşturma (onCreate'de bir kere oluşturulmalı)
        queue = Volley.newRequestQueue(this);

        // 81 ili strings.xml'den al
        String[] provinces = getResources().getStringArray(R.array.turkey_provinces);

        // Spinner için bir ArrayAdapter oluştur
        // android.R.layout.simple_spinner_dropdown_item, Spinner'ın varsayılan görünümüdür
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, provinces);

        // Adapter'ı Spinner'a bağla
        citySpinner.setAdapter(adapter);

        // Spinner'dan bir öğe seçildiğinde ne olacağını belirle
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Seçilen şehir adını al
                String selectedCity = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "Selected City: " + selectedCity);

                // Seçilen şehir için hava durumu isteğini yap
                // NOT: API isteği kodunu ayrı bir metoda taşıyacağız!
                getWeatherData(selectedCity); // Yeni metodumuzu çağırıyoruz
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Hiçbir şey seçili değilse (genellikle ilk açıldığında)
                weatherInfoTextView.setText("Lütfen bir şehir seçin.");
            }
        });

        // onCreate içindeki eski sabit şehir ile yapılan API isteğini SİLİN veya YORUM SATIRI YAPIN
        // getWeatherData("Istanbul"); // Artık başlangıçta şehir seçili olmayacak, Spinner'dan seçilince tetiklenecek
    }

    // Hava durumu verisini çeken ayrı bir metod oluşturalım
    private void getWeatherData(String cityName) {
        // API isteği URL'sini oluşturma
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + API_KEY + "&units=metric&lang=tr"; // lang=tr ile Türkçe açıklama almayı deneyelim

        // Eski isteği iptal et (eğer varsa ve hala çalışıyorsa - Volley bunu otomatik yönetir ama yine de iyi pratik)
        // queue.cancelAll(TAG); // İsteğe TAG ekleyerek iptal edilebilir, şimdilik gerekli değil

        // StringRequest oluşturma
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Başarılı yanıt geldiğinde burası çalışır
                        Log.d(TAG, "API Response for " + cityName + ": " + response);

                        // JSON yanıtını ayrıştırma
                        Gson gson = new Gson();
                        try {
                            WeatherResponse weatherResponse = gson.fromJson(response, WeatherResponse.class);

                            // Ayrıştırılmış verileri kullanarak UI'ı güncelleme
                            if (weatherResponse != null) {
                                String cityDisplayName = weatherResponse.getName();
                                double temperatureCelsius = weatherResponse.getMain().getTemp();
                                String description = "N/A";
                                if (weatherResponse.getWeather() != null && !weatherResponse.getWeather().isEmpty()) {
                                    description = weatherResponse.getWeather().get(0).getDescription();
                                }
                                int humidity = weatherResponse.getMain().getHumidity();

                                // Yazı formatını HTML kullanarak iyileştirme
                                String weatherText = "<b>Şehir:</b> " + cityDisplayName +
                                        "<br/><b>Sıcaklık:</b> " + String.format("%.1f", temperatureCelsius) + "°C" +
                                        "<br/><b>Açıklama:</b> " + description +
                                        "<br/><b>Nem:</b> %" + humidity;

                                // İkon kodunu al
                                String iconCode = null;
                                if (weatherResponse.getWeather() != null && !weatherResponse.getWeather().isEmpty()) {
                                    iconCode = weatherResponse.getWeather().get(0).getIcon(); // models/Weather.java'ya getIcon() metodunu eklediğinden emin ol
                                }

                                // İkonu gösterme (Glide kullanarak)
                                if (iconCode != null) {
                                    String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png"; // İkon URL'sini oluştur
                                    Glide.with(MainActivity.this) // Activity context'ini ver
                                            .load(iconUrl) // Yüklenecek URL
                                            .placeholder(R.drawable.ic_launcher_foreground) // Yüklenirken gösterilecek geçici resim (isteğe bağlı, kendi ikonunu koyabilirsin)
                                            .error(R.drawable.ic_launcher_background) // Hata olursa gösterilecek resim (isteğe bağlı)
                                            .into(weatherIconImageView); // Resmin gösterileceği ImageView
                                } else {
                                    // İkon kodu yoksa veya hata olursa ImageView'ı gizle veya varsayılan bir şey göster
                                    weatherIconImageView.setImageDrawable(null); // Veya weatherIconImageView.setVisibility(View.GONE);
                                }

                                // HTML formatlı metni TextView'e atarken HtmlCompat kullanmak daha güvenlidir
                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                    weatherInfoTextView.setText(Html.fromHtml(weatherText, Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    weatherInfoTextView.setText(Html.fromHtml(weatherText));
                                }

                            } else {
                                weatherInfoTextView.setText("Hava durumu bilgisi ayrıştırılamadı.");
                                Toast.makeText(MainActivity.this, "Hava durumu verisi geçersiz.", Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                            weatherInfoTextView.setText("Hata: Veri işlenemedi.");
                            Toast.makeText(MainActivity.this, "Veri işlenirken hata oluştu.", Toast.LENGTH_LONG).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "API Error for " + cityName + ": " + error.getMessage());
                weatherInfoTextView.setText(cityName + " için hava durumu alınamadı.");
                // Hata detayını kontrol edip kullanıcıya daha anlamlı mesaj gösterebilirsiniz
                // Örneğin 404 hatası (şehir bulunamadı) veya ağ hatası
                String errorMessage = "Hata oluştu.";
                if (error != null && error.networkResponse != null) {
                    // Hata durum kodunu kontrol et
                    int statusCode = error.networkResponse.statusCode;
                    if (statusCode == 404) {
                        errorMessage = cityName + " bulunamadı. Lütfen şehir adını kontrol edin.";
                    } else {
                        errorMessage = "API Hatası: " + statusCode;
                    }
                } else if (error != null && error.getMessage() != null) {
                    errorMessage = "Ağ Hatası: " + error.getMessage();
                }
                Toast.makeText(MainActivity.this, errorMessage, Toast.LENGTH_LONG).show();
            }
        });

        // İsteğe bir TAG eklemek, gerekirse iptal etmek için faydalı olabilir
        stringRequest.setTag(TAG); // Örneğin Activity'nin TAG'ini kullanabiliriz

        // İsteği RequestQueue'ya ekleme
        queue.add(stringRequest);
    }

    // Activity sonlandığında bekleyen istekleri iptal etmek iyi bir pratiktir
    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG); // TAG'e sahip tüm istekleri iptal et
        }
    }
}