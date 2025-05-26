package com.example.weatherapp;

// Mevcut importlar
import com.example.weatherapp.models.WeatherResponse;
import com.example.weatherapp.models.Weather;
import com.example.weatherapp.models.Main;

import android.security.KeyChain;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

// Firebase Authentication ve User için importlar
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.AuthCredential;

// Firebase Firestore için importlar <-- Bunlar eklendi/düzenlendi
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.gson.Gson;

// Menü ve PopupMenu için importlar
import android.view.MenuInflater;
import android.widget.PopupMenu;
import android.view.MenuItem;
import android.content.Intent;
import android.text.TextUtils;

// AlertDialog ve ilgili bileşenler için importlar
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.widget.EditText;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List; // ArrayList ve List kullanmak için eklendi
import java.util.Arrays; // Eğer direkt Arrays.asList kullanacaksanız. Şu an kullanılmıyor ama kalsın.

public class MainActivity extends AppCompatActivity {

    // Değişkenler
    private static final String TAG = "WeatherApp";

    private TextView weatherInfoTextView;
    private Spinner citySpinner;
    private ImageView weatherIconImageView;

    private TextView userInitialTextView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Firebase Firestore nesnesi eklendi

    private List<String> currentCityList; // Spinner için kullanılacak şehir listesi eklendi

    private static final String API_KEY = "524c1023a86e0d2228a9e22dfbef1d60"; // API Anahtarınız
    private RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            ProviderInstaller.installIfNeeded(this);
        } catch (GooglePlayServicesRepairableException e) {
            // Google Play Services on the device is old, disabled, or not available.
            // Kullanıcıya bir diyalog gösterebilirsiniz
            GoogleApiAvailability.getInstance().showErrorNotification(this, e.getConnectionStatusCode());
            Log.e(TAG, "ProviderInstaller.installIfNeeded error: " + e.getMessage());
        } catch (GooglePlayServicesNotAvailableException e) {
            // Google Play Services mevcut değil
            Log.e(TAG, "ProviderInstaller.installIfNeeded error: " + e.getMessage());
        }

        // Firebase Authentication ve Firestore nesnelerini al
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Firestore nesnesini başlat

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
            // Kullanıcı giriş yapmamışsa LoginActivity'ye yönlendir
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
            finish();
            return; // onCreate'i erken sonlandır
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

        // Spinner'ı başlangıçta boş bir liste ile ayarla
        // Kayıtlı şehirler yüklendikten sonra updateSpinnerWithCities metodu ile güncellenecek.
        currentCityList = new ArrayList<>(); // Başlangıçta boş liste
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, currentCityList);
        citySpinner.setAdapter(adapter);

        // Spinner öğesi seçildiğinde hava durumu bilgisini çekme (Güncellendi: Listenin boş olma durumunu kontrol eder)
        citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Spinner boş değilse ve geçerli bir pozisyon seçildiyse işlemi yap
                if (currentCityList != null && position >= 0 && position < currentCityList.size()) {
                    String selectedCity = currentCityList.get(position);
                    Log.d(TAG, "Selected City from Spinner: " + selectedCity);
                    getWeatherData(selectedCity); // Hava durumu metodunu çağır
                } else {
                    // Liste boşsa veya geçersiz seçimse varsayılan mesaj göster
                    Log.d(TAG, "Spinner'da seçilecek şehir yok veya geçersiz pozisyon.");
                    weatherInfoTextView.setText("Lütfen şehir ekleyin.");
                    weatherIconImageView.setImageDrawable(null);
                    weatherIconImageView.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                weatherInfoTextView.setText("Lütfen şehir ekleyin.");
                weatherIconImageView.setImageDrawable(null);
                weatherIconImageView.setVisibility(View.GONE);
            }
        });

        // Uygulama başladığında kullanıcının kayıtlı şehirlerini yükle <-- Burası eklendi
        loadSavedCities();
    }

    // Kullanıcının kayıtlı şehirlerini Firestore'dan yükleyen metod
    private void loadSavedCities() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Log.e(TAG, "loadSavedCities: No user logged in.");
            logoutUser(); // Güvenlik için Login'e geri yönlendir
            return;
        }

        String userId = user.getUid();

        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        List<String> savedCities = null;
                        Object citiesObject = documentSnapshot.get("savedCities");
                        if (citiesObject instanceof List) {
                            // Firestore'dan gelen veriyi List<String>'e cast et
                            savedCities = (List<String>) citiesObject;
                        }

                        if (savedCities != null && !savedCities.isEmpty()) {
                            Log.d(TAG, "Kayıtlı şehirler yüklendi: " + savedCities.toString());
                            updateSpinnerWithCities(savedCities);
                        } else {
                            Log.d(TAG, "Kullanıcının kayıtlı şehri yok veya liste boş.");
                            weatherInfoTextView.setText("Henüz kayıtlı şehriniz yok.\nMenüden şehir ekleyebilirsiniz.");
                            updateSpinnerWithCities(new ArrayList<>());
                        }
                    } else {
                        Log.d(TAG, "Kullanıcı için Firestore belgesi bulunamadı.");
                        weatherInfoTextView.setText("Henüz kayıtlı şehriniz yok.\nMenüden şehir ekleyebilirsiniz.");
                        updateSpinnerWithCities(new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Kayıtlı şehirler yüklenirken hata oluştu.", e);
                    Toast.makeText(MainActivity.this, "Kayıtlı şehirler yüklenirken hata oluştu.", Toast.LENGTH_SHORT).show();
                    weatherInfoTextView.setText("Kayıtlı şehirler yüklenirken hata oluştu.");
                    updateSpinnerWithCities(new ArrayList<>());
                });
    }

    // Spinner'ı verilen şehir listesiyle güncelleyen metod
    private void updateSpinnerWithCities(List<String> cities) {
        currentCityList.clear(); // Mevcut listeyi temizle
        if (cities != null) {
            currentCityList.addAll(cities); // Yeni şehirleri listeye ekle
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, currentCityList);
        citySpinner.setAdapter(adapter);

        if (currentCityList != null && !currentCityList.isEmpty()) {
            citySpinner.setSelection(0); // İlk öğeyi seç (onItemSelected otomatik tetiklenir)
        } else {
            weatherInfoTextView.setText("Lütfen şehir ekleyin.");
            weatherIconImageView.setImageDrawable(null);
            weatherIconImageView.setVisibility(View.GONE);
            Log.d(TAG, "Spinner listesi boş. Hava durumu çekilmiyor.");
        }
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
                    sendPasswordResetEmail();
                    return true;
                } else if (itemId == R.id.menu_add_city) {
                    showAddCityDialog(); // Şehir ekleme diyalogunu göster
                    return true;
                } else if (itemId == R.id.menu_delete_city) {
                    showDeleteCityDialog(); // <-- Şehir silme diyalogunu çağıran metot (Bir sonraki adımda eklenecek)
                    return true;
                } else if (itemId == R.id.menu_logout) {
                    logoutUser();
                    return true;
                } else if (itemId == R.id.menu_delete_account) {
                    confirmAndDeleteAccount();
                    return true;
                } else {
                    return false;
                }
            }
        });
        popup.show();
    }

    // Kullanıcının çıkış yapmasını sağlayan metod
    private void logoutUser() {
        mAuth.signOut();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
        Toast.makeText(this, "Çıkış yapıldı.", Toast.LENGTH_SHORT).show();
    }

    // Şifre sıfırlama e-postası gönderen metod
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

    // Hesabı silme işlemini başlatan metod
    private void confirmAndDeleteAccount() {
        FirebaseUser user = mAuth.getCurrentUser();

        if (user == null) {
            Toast.makeText(this, "Hesap silme için geçerli kullanıcı bulunamadı.", Toast.LENGTH_SHORT).show();
            logoutUser();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hesabı Sil");
        builder.setMessage("Hesabınızı kalıcı olarak silmek üzeresiniz. Bu işlem geri alınamaz. Devam etmek için lütfen şifrenizi girin.");

        final EditText passwordEditText = new EditText(this);
        passwordEditText.setHint("Şifreniz");
        passwordEditText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        int paddingDp = 16;
        float density = getResources().getDisplayMetrics().density;
        int paddingPixel = (int) (paddingDp * density);
        layout.setPadding(paddingPixel, 0, paddingPixel, 0);
        layout.addView(passwordEditText);
        builder.setView(layout);

        builder.setPositiveButton("Sil", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Hesabı silmek için şifre girmelisiniz.", Toast.LENGTH_SHORT).show();
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(user.getEmail(), password);

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "Kullanıcı yeniden kimlik doğrulandı.");

                                    user.delete()
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "Kullanıcı hesabı silindi.");
                                                        Toast.makeText(MainActivity.this, "Hesabınız başarıyla silindi.", Toast.LENGTH_LONG).show();

                                                        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        startActivity(intent);
                                                        finish();

                                                    } else {
                                                        Log.e(TAG, "Kullanıcı hesabı silinemedi.", task.getException());
                                                        Toast.makeText(MainActivity.this, "Hesap silme başarısız: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Log.e(TAG, "Yeniden kimlik doğrulama başarısız.", task.getException());
                                    Toast.makeText(MainActivity.this, "Kimlik doğrulama başarısız. Lütfen şifrenizi kontrol edin.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });

        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Şehir ekleme diyalogunu gösteren metod (Güncellendi: Mevcut şehirleri işaretler ve kayıt sonrası Spinner'ı günceller)
    private void showAddCityDialog() {
        final String[] allProvinces = getResources().getStringArray(R.array.turkey_provinces);

        final boolean[] selectedItems = new boolean[allProvinces.length];
        if (currentCityList != null) {
            for (int i = 0; i < allProvinces.length; i++) {
                if (currentCityList.contains(allProvinces[i])) {
                    selectedItems[i] = true;
                } else {
                    selectedItems[i] = false;
                }
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Şehirleri Seçin");

        builder.setMultiChoiceItems(allProvinces, selectedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                selectedItems[which] = isChecked;
            }
        });

        builder.setPositiveButton("Tamam", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<String> selectedCities = new ArrayList<>();
                for (int i = 0; i < allProvinces.length; i++) {
                    if (selectedItems[i]) {
                        selectedCities.add(allProvinces[i]);
                    }
                }
                saveSelectedCitiesToFirestore(selectedCities); // Şehirleri kaydet ve kayıt sonrası Spinner'ı güncelle
                Log.d(TAG, "Seçilen Şehirler kaydediliyor: " + selectedCities.toString());
            }
        });

        builder.setNegativeButton("İptal", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    // Seçilen şehirleri Firebase Firestore'a kaydeden metod (Güncellendi: Kayıt sonrası Spinner'ı otomatik günceller)
    private void saveSelectedCitiesToFirestore(ArrayList<String> cities) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            Toast.makeText(this, "Şehir kaydetmek için giriş yapmalısınız.", Toast.LENGTH_SHORT).show();
            logoutUser();
            return;
        }
        String userId = user.getUid();
        Map<String, Object> cityData = new HashMap<>();
        cityData.put("savedCities", cities);

        db.collection("users").document(userId)
                .set(cityData) // set() metodu mevcutsa günceller, yoksa oluşturur.
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Şehirler başarıyla kaydedildi!");
                    Toast.makeText(MainActivity.this, "Seçilen şehirler kaydedildi.", Toast.LENGTH_SHORT).show();
                    loadSavedCities(); // Kayıt başarılı olduktan sonra şehir listesini yeniden yükle ve Spinner'ı güncelle
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Şehirler kaydedilirken hata oluştu.", e);
                    Toast.makeText(MainActivity.this, "Şehirler kaydedilirken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    // TODO: Şehir Silme Diyaloğu ve Firestore'dan Silme Metotları eklenecek (Şimdilik boş hali)
    private void showDeleteCityDialog() {
        Toast.makeText(this, "Şehir Sil seçildi (Yakında eklenecek)", Toast.LENGTH_SHORT).show();
        // Bu metod, kullanıcının kayıtlı şehirlerini listeleyen ve silmek istediklerini seçmesini sağlayan bir diyalog gösterecek.
        // Seçilen şehirler Firestore'dan kaldırılacak ve akabinde loadSavedCities() çağrılarak Spinner güncellenecek.
    }


    // Hava durumu verisini çeken ayrı bir metod
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

                                String weatherText = "<b>İl:</b> " + cityDisplayName +
                                        "<br/><b>Sıcaklık:</b> " + String.format("%.1f", temperatureCelsius) + "°C" +
                                        "<br/><b>Durum:</b> " + description +
                                        "<br/><b>Nem:</b> %" + humidity;

                                String iconCode = null;
                                if (weatherResponse.getWeather() != null && !weatherResponse.getWeather().isEmpty() && weatherResponse.getWeather().get(0) != null) {
                                    iconCode = weatherResponse.getWeather().get(0).getIcon();
                                }

                                if (iconCode != null && !iconCode.isEmpty()) {
                                    String iconUrl = "https://openweathermap.org/img/wn/" + iconCode + "@2x.png";
                                    Glide.with(MainActivity.this)
                                            .load(iconUrl)
                                            .placeholder(R.drawable.ic_launcher_foreground)
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

    // Activity sonlandığında bekleyen istekleri iptal etmek
    @Override
    protected void onStop () {
        super.onStop();
        if (queue != null) {
            queue.cancelAll(TAG);
        }
    }
}