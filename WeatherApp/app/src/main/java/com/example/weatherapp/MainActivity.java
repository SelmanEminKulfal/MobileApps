package com.example.weatherapp;

// Mevcut importlar
import com.example.weatherapp.models.WeatherResponse;
import com.example.weatherapp.models.Weather;
import com.example.weatherapp.models.Main;

import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull; // <-- @NonNull notasyonu için ekleyin
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

// Firebase Authentication ve User için importlar
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.EmailAuthProvider; // <-- Şifre ile kimlik doğrulama için eklendi
import com.google.firebase.auth.AuthCredential; // <-- Kimlik bilgisi türü için eklendi

import com.google.gson.Gson;

// Menü ve PopupMenu için importlar
import android.view.MenuInflater;
import android.widget.PopupMenu;
import android.view.MenuItem;
import android.content.Intent; // Logout sonrası geçiş için
import android.text.TextUtils; // Metin kontrolleri için eklendi

// AlertDialog ve ilgili bileşenler için importlar
import android.app.AlertDialog; // <-- AlertDialog için eklendi
import android.content.DialogInterface; // <-- Dialog butonları için eklendi
import android.widget.EditText; // <-- Dialog içine EditText için eklendi
import android.widget.LinearLayout; // <-- Dialog içindeki layout için eklendi

public class MainActivity extends AppCompatActivity {

    // Değişkenler
    private static final String TAG = "WeatherApp";

    private TextView weatherInfoTextView;
    private Spinner citySpinner;
    private ImageView weatherIconImageView;

    private TextView userInitialTextView;

    private FirebaseAuth mAuth; // Firebase Authentication nesnesi

    private static final String API_KEY = "524c1023a86e0d2228a9e22dfbef1d60"; // API Anahtarınız
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase Authentication nesnesini al
        mAuth = FirebaseAuth.getInstance();

        // Layout dosyasındaki bileşenleri bul
        weatherInfoTextView = findViewById(R.id.weatherInfoTextView);
        citySpinner = findViewById(R.id.citySpinner);
        weatherIconImageView = findViewById(R.id.weatherIconImageView);
        userInitialTextView = findViewById(R.id.userInitialTextView);

        // Kullanıcının e-postasının ilk harfini al ve TextView'a yerleştir
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null && currentUser.getEmail() != null && !currentUser.getEmail().isEmpty()) {
            String email = currentUser.getEmail();
            char initial = email.charAt(0);
            userInitialTextView.setText(String.valueOf(initial).toUpperCase());
        } else {
            userInitialTextView.setText("?");
            // Kullanıcı giriş yapmamışsa MainActivity'de olmaması gerekir, LoginActivity'deki checkCurrentUser bunu yönlendirmeli
            // Yine de burada bir kontrol veya yönlendirme eklemek istersen ekleyebilirsin.
            // Örneğin:
            // Intent intent = new Intent(this, LoginActivity.class);
            // startActivity(intent);
            // finish();
        }

        // Kullanıcı ikonuna/iline tıklanma olayını dinle
        userInitialTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showOptionsMenu(v);
            }
        });

        // Volley RequestQueue oluşturma
        queue = Volley.newRequestQueue(this);

        // 81 ili strings.xml'den alma ve Spinner'ı ayarlama
        String[] provinces = getResources().getStringArray(R.array.turkey_provinces);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, provinces);
        citySpinner.setAdapter(adapter);

        // Spinner öğesi seçildiğinde hava durumu bilgisini çekme
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedCity = parent.getItemAtPosition(position).toString();
                Log.d(TAG, "Selected City: " + selectedCity);
                getWeatherData(selectedCity);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                weatherInfoTextView.setText("Lütfen bir şehir seçin.");
            }
        });

        // Uygulama ilk başladığında varsayılan bir şehri yüklemek istersen
        // Spinner varsayılan olarak ilk öğeyi seçeceği için, bu listener zaten tetiklenecek.
        // Eğer Spinner'da varsayılan bir şehir seçmek istersen:
        // citySpinner.setSelection(adapter.getPosition("Ankara")); // "Ankara" stringinin listedeki pozisyonuna göre
    }

    // Seçenekler menüsünü gösteren metod
    private void showOptionsMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.options_menu, popup.getMenu());

        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.menu_password_reset) {
                    // Şifre yenileme işlemi
                    sendPasswordResetEmail(); // <-- Metodu çağırıyoruz
                    return true;
                } else if (itemId == R.id.menu_add_city) {
                    // Şehir ekleme işlemi (TODO: Veritabanı gerektirir)
                    Toast.makeText(MainActivity.this, "Şehir Ekle seçildi (Henüz aktif değil)", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.menu_delete_city) {
                    // Şehir silme işlemi (TODO: Veritabanı gerektirir)
                    Toast.makeText(MainActivity.this, "Şehir Sil seçildi (Henüz aktif değil)", Toast.LENGTH_SHORT).show();
                    return true;
                } else if (itemId == R.id.menu_logout) {
                    // Çıkış yap işlemi
                    logoutUser(); // <-- Metodu çağırıyoruz
                    return true;
                } else if (itemId == R.id.menu_delete_account) {
                    // Hesabı silme işlemi
                    confirmAndDeleteAccount(); // <-- Metodu çağırıyoruz
                    return true;
                } else {
                    return false;
                }
            }
        });
        popup.show();
    }

    // Kullanıcının çıkış yapmasını sağlayan metod (Daha önce eklemiştik)
    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Çıkış yapıldı.", Toast.LENGTH_SHORT).show();
    }

    // Şifre sıfırlama e-postası gönderen metod <-- Bu metodu ekleyin
    private void sendPasswordResetEmail() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null && user.getEmail() != null && !user.getEmail().isEmpty()) {
            String emailAddress = user.getEmail();

            mAuth.sendPasswordResetEmail(emailAddress)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Şifre sıfırlama e-postası gönderildi: " + emailAddress);
                                Toast.makeText(MainActivity.this, "Şifre sıfırlama e-postası gönderildi. Lütfen e-postanızı kontrol edin.", Toast.LENGTH_LONG).show();
                            } else {
                                Log.e(TAG, "Şifre sıfırlama e-postası gönderilemedi.", task.getException());
                                Toast.makeText(MainActivity.this, "Şifre sıfırlama e-postası gönderilemedi. Hata: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else {
            Toast.makeText(this, "Şifre sıfırlama için geçerli kullanıcı bulunamadı.", Toast.LENGTH_SHORT).show();
        }
    }

    // Hesabı silme işlemini başlatan metod <-- Bu metodu ekleyin
    private void confirmAndDeleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Hesap silme için geçerli kullanıcı bulunamadı.", Toast.LENGTH_SHORT).show();
            logoutUser(); // Kullanıcı null ise Login'e yönlendir
            return;
        }

        // Kullanıcıdan şifresini tekrar girmesini isteyen bir iletişim kutusu oluştur
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hesabı Sil");
        builder.setMessage("Hesabınızı kalıcı olarak silmek üzeresiniz. Bu işlem geri alınamaz. Devam etmek için lütfen şifrenizi girin.");

        // İletişim kutusu içine şifre girmek için EditText ekle
        final EditText passwordEditText = new EditText(this);
        passwordEditText.setHint("Şifreniz");
        passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD); // Şifre girişi için uygun input tipi
        passwordEditText.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));

        // EditText'i iletişim kutusuna eklemek için bir Layout kullan ve padding ver
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        // Layout'a padding eklemek, EditText'in kenarlara yapışmasını engeller
        int paddingDp = 16; // dp cinsinden padding miktarı
        float density = getResources().getDisplayMetrics().density;
        int paddingPixel = (int) (paddingDp * density);
        layout.setPadding(paddingPixel, 0, paddingPixel, 0);
        layout.addView(passwordEditText);
        builder.setView(layout);

        // Pozitif buton (Sil)
        builder.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Hesabı silmek için şifre girmelisiniz.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kullanıcıyı yeniden kimlik doğrulama (re-authenticate)
                // Hesabı silme gibi hassas işlemler için yakın zamanda giriş yapılmış olması gerekir.
                // Yakın zamanda giriş yapılmadıysa re-authenticate yapmalıyız.
                // E-posta alanı EditText olmadığı için kullanıcının mevcut e-postasını FirebaseUser nesnesinden alıyoruz
                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password); // E-posta ve şifre ile kimlik bilgisi oluştur

                user.reauthenticate(credential) // Kullanıcıyı yeniden kimlik doğrula
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Kullanıcı yeniden kimlik doğrulandı.");

                                    // Yeniden kimlik doğrulama başarılı, şimdi hesabı sil
                                    user.delete() // Hesabı silme işlemi
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "Kullanıcı hesabı silindi.");
                                                        Toast.makeText(MainActivity.this, "Hesabınız başarıyla silindi.", Toast.LENGTH_LONG).show();

                                                        // Hesap silindikten sonra Login ekranına yönlendir
                                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                        finish();

                                                    } else {
                                                        Log.e(TAG, "Kullanıcı hesabı silinemedi.", task.getException());
                                                        // task.getException() içinde FirebaseAuthRecentLoginRequiredException gibi hatalar olabilir (yeniden kimlik doğrulama süresi dolduysa)
                                                        Toast.makeText(MainActivity.this, "Hesap silme başarısız: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Log.e(TAG, "Yeniden kimlik doğrulama başarısız.", task.getException());
                                    Toast.makeText(MainActivity.this, "Kimlik doğrulama başarısız. Lütfen şifrenizi kontrol edin.", Toast.LENGTH_LONG).show();
                                    // Genellikle şifre yanlış girilmiştir
                                }
                            }
                        });
            }
        });

        // Negatif buton (İptal)
        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel(); // Dialogu kapat
            }
        });

        // AlertDialog'u göster
        builder.show();
    }


    // Hava durumu verisini çeken ayrı bir metod (Mevcut kod)
    private void getWeatherData(String cityName) {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&appid=" + API_KEY + "&units=metric&lang=tr";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "API Response for " + cityName + ": " + response);
                        Gson gson = new Gson();
                        try {
                            WeatherResponse weatherResponse = gson.fromJson(response, WeatherResponse.class);

                            if (weatherResponse != null) {
                                String cityDisplayName = weatherResponse.getName();
                                double temperatureCelsius = weatherResponse.getMain().getTemp();
                                String description = "N/A";
                                if (weatherResponse.getWeather() != null && !weatherResponse.getWeather().isEmpty() && weatherResponse.getWeather().get(0) != null) {
                                    description = weatherResponse.getWeather().get(0).getDescription();
                                }
                                int humidity = weatherResponse.getMain().getHumidity();

                                // Yazı formatını HTML kullanarak iyileştirme (Mevcut kod)
                                String weatherText = "<b>İl:</b> " + cityDisplayName +
                                        "<br/><b>Sıcaklık:</b> " + String.format("%.1f", temperatureCelsius) + "°C" +
                                        "<br/><b>Durum:</b> " + description +
                                        "<br/><b>Nem:</b> %" + humidity;

                                // İkon kodunu al ve göster (Mevcut kod)
                                String iconCode = null;
                                if (weatherResponse.getWeather() != null && !weatherResponse.getWeather().isEmpty() && weatherResponse.getWeather().get(0) != null) {
                                    iconCode = weatherResponse.getWeather().get(0).getIcon();
                                }

                                if (iconCode != null && !iconCode.isEmpty()) {
                                    String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                                    Glide.with(MainActivity.this)
                                            .load(iconUrl)
                                            .placeholder(R.drawable.ic_launcher_foreground) // Placeholder ve error ikonlarını kendi ikonlarınla değiştirmeyi unutma
                                            .error(R.drawable.ic_launcher_background)
                                            .into(weatherIconImageView);
                                    weatherIconImageView.setVisibility(View.VISIBLE);
                                } else {
                                    weatherIconImageView.setImageDrawable(null);
                                    weatherIconImageView.setVisibility(View.GONE);
                                }

                                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                                    weatherInfoTextView.setText(Html.fromHtml(weatherText, Html.FROM_HTML_MODE_COMPACT));
                                } else {
                                    weatherInfoTextView.setText(Html.fromHtml(weatherText));
                                }

                            } else {
                                weatherInfoTextView.setText("Hava durumu bilgisi ayrıştırılamadı.");
                                Toast.makeText(MainActivity.this, "Hava durumu verisi geçersiz.", Toast.LENGTH_SHORT).show();
                                weatherIconImageView.setImageDrawable(null);
                                weatherIconImageView.setVisibility(View.GONE);
                            }

                        } catch (Exception e) {
                            Log.e(TAG, "JSON Parsing Error: " + e.getMessage());
                            weatherInfoTextView.setText("Hata: Veri işlenemedi.");
                            Toast.makeText(MainActivity.this, "Veri işlenirken hata oluştu.", Toast.LENGTH_LONG).show();
                            weatherIconImageView.setImageDrawable(null);
                            weatherIconImageView.setVisibility(View.GONE);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "API Error for " + cityName + ": " + error.getMessage());
                weatherInfoTextView.setText(cityName + " için hava durumu alınamadı.");

                String errorMessage = "Hata oluştu.";
                if (error != null && error.networkResponse != null) {
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

                weatherIconImageView.setImageDrawable(null);
                weatherIconImageView.setVisibility(View.GONE);
            }
        });

        stringRequest.setTag(TAG);
        queue.add(stringRequest);
    }

    // Activity sonlandığında bekleyen istekleri iptal etmek (Mevcut kod)
    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }


}