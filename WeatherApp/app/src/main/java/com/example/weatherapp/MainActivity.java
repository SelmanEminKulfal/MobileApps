package com.example.weatherapp; // Paket adınızı kendi projenize göre değiştirin

import com.bumptech.glide.Glide; // Glide import'unu ekleyin
// Eski model importları yerine WeatherAPI.com modellerini kullanacağız
import com.example.weatherapp.model.Current; // Yeni eklenen model importu
import com.example.weatherapp.model.Location; // Yeni eklenen model importu
import com.example.weatherapp.model.WeatherResponse; // Yeni eklenen model importu
import com.example.weatherapp.service.WeatherApiService; // API servisiniz

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private ImageView ivWeatherIcon; // Hava durumu ikonu için ImageView

    // WeatherAPI.com için API anahtarı ve BASE_URL güncellendi
    private final String API_KEY = BuildConfig.WEATHER_API_KEY; // Your WeatherAPI.com API Key
    private static final String BASE_URL = "https://api.weatherapi.com/v1/";

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private FirebaseUser currentUser;

    // UI Bileşenleri
    private Spinner spinnerCities;
    private ImageView ivProfileIcon;
    private TextView tvCityName, tvTemperature, tvDescription;

    // Şehir Verileri
    private List<City> userCities;
    private ArrayAdapter<String> cityAdapter;
    private List<String> cityNames;

    // WeatherAPI.com API Servisi
    private WeatherApiService weatherApiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ivWeatherIcon = findViewById(R.id.iv_weather_icon); // Hava durumu ikonunu bağlama

        // Retrofit ve API Servisi Başlatma - BASE_URL WeatherAPI.com'a göre ayarlandı
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        weatherApiService = retrofit.create(WeatherApiService.class);

        // Firebase Instance'larını Başlatma
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();

        // Eğer kullanıcı oturum açmamışsa giriş ekranına yönlendir
        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            finish();
            return;
        }

        // UI Elemanlarını Bağlama
        spinnerCities = findViewById(R.id.spinner_cities);
        ivProfileIcon = findViewById(R.id.iv_profile_icon);
        tvCityName = findViewById(R.id.tv_city_name);
        tvTemperature = findViewById(R.id.tv_temperature);
        tvDescription = findViewById(R.id.tv_description);

        userCities = new ArrayList<>();
        cityNames = new ArrayList<>();
        cityAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, cityNames);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCities.setAdapter(cityAdapter);

        // Kullanıcının şehirlerini Firestore'dan çek
        loadUserCities();

        // Spinner'da şehir seçimi dinleyicisi
        spinnerCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position >= 0 && position < userCities.size()) {
                    String selectedCityName = userCities.get(position).getName();
                    // Seçilen şehrin hava durumu verilerini çek
                    fetchWeatherData(selectedCityName);
                } else {
                    tvCityName.setText("Şehir Seçilmedi");
                    tvTemperature.setText("Sıcaklık: --°C");
                    tvDescription.setText("Durum: --");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Hiçbir şey seçilmediğinde
            }
        });

        // Profil ikonuna tıklama dinleyicisi
        ivProfileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showProfileMenu(v);
            }
        });
    }

    // --- Firebase Veritabanı İşlemleri ---

    /**
     * Kullanıcının Firebase Firestore'daki kayıtlı şehirlerini yükler.
     * Şehirler yüklendikten sonra Spinner'ı günceller ve varsa ilk şehrin hava durumunu çeker.
     */
    private void loadUserCities() {
        if (currentUser == null) {
            return;
        }

        db.collection("users")
                .document(currentUser.getUid())
                .collection("cities")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            userCities.clear();
                            cityNames.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                City city = document.toObject(City.class);
                                city.setId(document.getId()); // Firestore belge ID'sini kaydet
                                userCities.add(city);
                                cityNames.add(city.getName());
                            }
                            cityAdapter.notifyDataSetChanged(); // Spinner'ı güncelle

                            if (!userCities.isEmpty()) {
                                spinnerCities.setSelection(0); // İlk şehri otomatik seç
                                // İlk şehir yüklendiğinde hava durumu verilerini çek
                                fetchWeatherData(userCities.get(0).getName());
                            } else {
                                Toast.makeText(MainActivity.this, "Henüz şehir eklenmedi.", Toast.LENGTH_SHORT).show();
                                tvCityName.setText("Şehir Ekleyin");
                                tvTemperature.setText("Sıcaklık: --°C");
                                tvDescription.setText("Durum: --");
                                ivWeatherIcon.setImageDrawable(null); // İkonu temizle
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Şehirler yüklenirken hata oluştu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Seçilen şehri Firebase Firestore'a ekler.
     * Başarılı olursa şehirleri yeniden yükler (Spinner'ı günceller).
     * @param cityName Eklenecek şehrin adı.
     */
    private void addCityToFirestore(String cityName) {
        if (currentUser == null) {
            Toast.makeText(this, "Giriş yapmanız gerekiyor.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> cityMap = new HashMap<>();
        cityMap.put("name", cityName);

        db.collection("users")
                .document(currentUser.getUid())
                .collection("cities")
                .add(cityMap) // Yeni belge ekle, otomatik ID oluşturulacak
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, cityName + " başarıyla eklendi.", Toast.LENGTH_SHORT).show();
                            loadUserCities(); // Şehirleri yeniden yükle (Spinner'ı güncelle)
                        } else {
                            Toast.makeText(MainActivity.this, "Şehir eklenirken hata oluştu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Firebase Firestore'dan belirtilen şehir ID'sine sahip şehri siler.
     * Başarılı olursa şehirleri yeniden yükler (Spinner'ı günceller).
     * @param cityId Silinecek şehrin Firestore belge ID'si.
     * @param cityName Silinen şehrin adı (Toast mesajı için).
     */
    private void deleteCityFromFirestore(String cityId, String cityName) {
        if (currentUser == null) {
            Toast.makeText(this, "Giriş yapmanız gerekiyor.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users")
                .document(currentUser.getUid())
                .collection("cities")
                .document(cityId) // Belge ID'sine göre sil
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, cityName + " başarıyla silindi.", Toast.LENGTH_SHORT).show();
                            loadUserCities(); // Şehirleri yeniden yükle (Spinner'ı güncelle)
                        } else {
                            Toast.makeText(MainActivity.this, "Şehir silinirken hata oluştu: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Kullanıcının Firebase Authentication hesabını siler.
     * Silmeden önce kullanıcının Firestore'daki tüm şehirlerini de siler.
     * İşlem başarılı olursa kullanıcıyı kayıt ekranına yönlendirir.
     */
    private void deleteUserCitiesThenAccount() {
        if (currentUser == null) {
            return;
        }

        // Önce kullanıcının şehirlerini sil
        CollectionReference citiesRef = db.collection("users").document(currentUser.getUid()).collection("cities");
        citiesRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete(); // Her şehri sil
                    }
                    // Tüm şehirler silindikten sonra hesabı sil
                    currentUser.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Hesabınız başarıyla silindi.", Toast.LENGTH_SHORT).show();
                                        // Kullanıcıyı kayıt ekranına yönlendir
                                        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Hesap silinirken hata oluştu: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                } else {
                    // Şehirleri silerken hata oluşsa bile hesabı silmeyi dene
                    Toast.makeText(MainActivity.this, "Şehirler silinirken hata oluştu, yine de hesabı silmeye çalışılıyor: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    currentUser.delete()
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Hesabınız başarıyla silindi.", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(MainActivity.this, RegisterActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Hesap silinirken hata oluştu: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
            }
        });
    }

    // --- WeatherAPI.com API İşlemleri ---

    /**
     * WeatherAPI.com API'sinden belirtilen şehrin güncel hava durumu verilerini çeker
     * ve UI'daki ilgili TextView'leri günceller.
     * @param cityName Hava durumu verileri çekilecek şehrin adı.
     */
    private void fetchWeatherData(String cityName) {
        // WeatherAPI.com'a göre API çağrısı parametreleri güncellendi
        Call<WeatherResponse> call = weatherApiService.getCurrentWeatherData(
                API_KEY, // BuildConfig.WEATHER_API_KEY'den geliyor
                cityName,
                "no" // "no" ile hava kalitesi verisi alınmaz, "yes" ile alınır
        );

        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    WeatherResponse weatherResponse = response.body();

                    // WeatherAPI.com model yapısına göre verilere erişim
                    Location location = weatherResponse.getLocation();
                    Current current = weatherResponse.getCurrent();

                    if (location != null && current != null && current.getCondition() != null) {
                        double temperature = current.getTempC(); // Santigrat sıcaklık
                        String description = current.getCondition().getText(); // Hava durumu açıklaması
                        String iconUrl = "https:" + current.getCondition().getIcon(); // İkon URL'si

                        // UI'ı güncelle
                        tvCityName.setText(location.getName());
                        tvTemperature.setText(String.format(Locale.getDefault(), "Sıcaklık: %.1f°C", temperature));
                        tvDescription.setText("Durum: " + capitalizeFirstLetter(description));

                        // Hava durumu ikonunu yükle
                        Log.d("WeatherApp", "Icon URL: " + iconUrl);
                        Glide.with(MainActivity.this)
                                .load(iconUrl)
                                //.placeholder(R.drawable.ic_launcher_foreground) // Yüklenirken gösterilecek resim (isteğe bağlı)
                                //.error(R.drawable.ic_launcher_foreground) // Hata oluştuğunda gösterilecek resim (isteğe bağlı)
                                .into(ivWeatherIcon);
                    } else {
                        Toast.makeText(MainActivity.this, "Hava durumu verileri eksik veya hatalı.", Toast.LENGTH_SHORT).show();
                        tvCityName.setText("Veri Yok");
                        tvTemperature.setText("Sıcaklık: --°C");
                        tvDescription.setText("Durum: --");
                        ivWeatherIcon.setImageDrawable(null); // İkonu temizle
                    }

                } else {
                    // WeatherAPI.com'da 400 ve 401 gibi kodlarla hata mesajları döndürülebilir.
                    String errorMessage = "Hava durumu verileri alınamadı.";
                    try {
                        if (response.errorBody() != null) {
                            errorMessage += " Hata: " + response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e("WeatherApp", "Error parsing error body: " + e.getMessage());
                    }
                    Toast.makeText(MainActivity.this, errorMessage + " Hata kodu: " + response.code(), Toast.LENGTH_LONG).show();
                    tvCityName.setText("Veri Yok");
                    tvTemperature.setText("Sıcaklık: --°C");
                    tvDescription.setText("Durum: --");
                    ivWeatherIcon.setImageDrawable(null); // İkonu temizle
                }
            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Hava durumu verileri çekilirken ağ hatası oluştu: " + t.getMessage(), Toast.LENGTH_LONG).show();
                t.printStackTrace();
                tvCityName.setText("Hata!");
                tvTemperature.setText("Sıcaklık: --°C");
                tvDescription.setText("Durum: --");
                ivWeatherIcon.setImageDrawable(null); // İkonu temizle
            }
        });
    }

    // --- Yardımcı Metotlar ve Dialoglar ---

    /**
     * Profil ikonuna tıklandığında açılan menüyü gösterir.
     * Menüdeki seçeneklere göre ilgili metotları çağırır.
     * @param v Menüyü tetikleyen View (profil ikonu).
     */
    private void showProfileMenu(View v) {
        PopupMenu popup = new PopupMenu(this, v);
        popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int id = item.getItemId();
                if (id == R.id.action_add_city) {
                    showAddCityDialog();
                    return true;
                } else if (id == R.id.action_delete_city) {
                    showDeleteCityDialog();
                    return true;
                } else if (id == R.id.action_update_password) {
                    updatePassword();
                    return true;
                } else if (id == R.id.action_delete_account) {
                    showDeleteAccountDialog();
                    return true;
                } else if (id == R.id.action_logout) {
                    logoutUser();
                    return true;
                }
                return false;
            }
        });
        popup.show();
    }

    /**
     * Kullanıcının ekleyebileceği Türkiye şehirlerini gösteren bir dialog penceresi açar.
     * Kullanıcının zaten eklediği şehirler listeden hariç tutulur.
     * Kullanıcı birden fazla şehir seçebilir.
     */
    private void showAddCityDialog() {
        final List<String> allTurkishCities = Arrays.asList(
                "Adana", "Adıyaman", "Afyonkarahisar", "Ağrı", "Amasya", "Ankara", "Antalya", "Artvin",
                "Aydın", "Balıkesir", "Bilecik", "Bingöl", "Bitlis", "Bolu", "Burdur", "Bursa", "Çanakkale",
                "Çankırı", "Çorum", "Denizli", "Diyarbakır", "Düzce", "Edirne", "Elazığ", "Erzincan", "Erzurum",
                "Eskişehir", "Gaziantep", "Giresun", "Gümüşhane", "Hakkari", "Hatay", "Iğdır", "Isparta",
                "İstanbul", "İzmir", "Kahramanmaraş", "Karabük", "Karaman", "Kars", "Kastamonu", "Kayseri",
                "Kilis", "Kırıkkale", "Kırklareli", "Kırşehir", "Kocaeli", "Konya", "Kütahya", "Malatya",
                "Manisa", "Mardin", "Mersin", "Muğla", "Muş", "Nevşehir", "Niğde", "Ordu", "Osmaniye",
                "Rize", "Sakarya", "Samsun", "Şanlıurfa", "Siirt", "Sinop", "Sivas", "Şırnak", "Tekirdağ",
                "Tokat", "Trabzon", "Tunceli", "Uşak", "Van", "Yalova", "Yozgat", "Zonguldak"
        );

        List<String> availableCities = new ArrayList<>(allTurkishCities);
        for (City userCity : userCities) {
            availableCities.remove(userCity.getName());
        }

        if (availableCities.isEmpty()) {
            Toast.makeText(this, "Tüm şehirleri eklediniz!", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Şehir Ekle");

        final CharSequence[] cityOptions = availableCities.toArray(new CharSequence[0]);
        boolean[] selectedCities = new boolean[availableCities.size()];

        builder.setMultiChoiceItems(cityOptions, selectedCities, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                selectedCities[which] = isChecked;
            }
        });

        builder.setPositiveButton("Ekle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int i = 0; i < selectedCities.length; i++) {
                    if (selectedCities[i]) {
                        addCityToFirestore(availableCities.get(i));
                    }
                }
            }
        });
        builder.setNegativeButton("İptal", null);
        builder.show();
    }

    /**
     * Kullanıcının eklediği şehirleri listeleyen ve silme işlemi yapılmasını sağlayan bir dialog penceresi açar.
     */
    private void showDeleteCityDialog() {
        if (userCities.isEmpty()) {
            Toast.makeText(this, "Silinecek şehir bulunmamaktadır.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Şehir Sil");

        final CharSequence[] cityOptions = new CharSequence[userCities.size()];
        for (int i = 0; i < userCities.size(); i++) {
            cityOptions[i] = userCities.get(i).getName();
        }

        builder.setItems(cityOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                City cityToDelete = userCities.get(which);
                deleteCityFromFirestore(cityToDelete.getId(), cityToDelete.getName());
            }
        });
        builder.setNegativeButton("İptal", null);
        builder.show();
    }

    /**
     * Firebase Authentication üzerinden kayıtlı e-posta adresine şifre sıfırlama linki gönderir.
     */
    private void updatePassword() {
        if (currentUser == null || currentUser.getEmail() == null) {
            Toast.makeText(this, "Giriş yapmanız gerekiyor veya e-posta bulunamadı.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Şifre Güncelleme");
        builder.setMessage("Kayıtlı e-posta adresinize ( " + currentUser.getEmail() + " ) şifre sıfırlama linki gönderilecektir. Devam etmek istiyor musunuz?");
        builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mAuth.sendPasswordResetEmail(currentUser.getEmail())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(MainActivity.this, "Şifre sıfırlama linki e-posta adresinize gönderildi.", Toast.LENGTH_LONG).show();
                                } else {
                                    Toast.makeText(MainActivity.this, "Şifre sıfırlama linki gönderilirken hata oluştu: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        builder.setNegativeButton("Hayır", null);
        builder.show();
    }

    /**
     * Kullanıcıdan şifresini isteyerek kimliğini yeniden doğrular ve ardından hesabı silme işlemini başlatır.
     */
    private void showDeleteAccountDialog() {
        if (currentUser == null) {
            Toast.makeText(this, "Giriş yapmanız gerekiyor.", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hesabı Sil");
        builder.setMessage("Hesabınızı kalıcı olarak silmek üzeresiniz. Bu işlem geri alınamaz. Devam etmek için lütfen şifrenizi girin.");

        final EditText passwordInput = new EditText(this);
        passwordInput.setHint("Şifre");
        passwordInput.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(passwordInput);

        builder.setPositiveButton("Hesabı Sil", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String password = passwordInput.getText().toString().trim();
                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(MainActivity.this, "Lütfen şifrenizi girin.", Toast.LENGTH_SHORT).show();
                    return;
                }
                reauthenticateAndDeleteAccount(password);
            }
        });
        builder.setNegativeButton("İptal", null);
        builder.show();
    }

    /**
     * Firebase Authentication kimlik doğrulamasını yeniden yapar ve başarılı olursa hesabı silme işlemini çağırır.
     * @param password Kullanıcının yeniden doğrulama için girdiği şifre.
     */
    private void reauthenticateAndDeleteAccount(String password) {
        if (currentUser == null) {
            return;
        }

        AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), password);

        currentUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            deleteUserCitiesThenAccount(); // Kimlik doğrulama başarılı, şimdi hesabı sil
                        } else {
                            Toast.makeText(MainActivity.this, "Şifre yanlış. Hesap silinemedi.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    /**
     * Kullanıcının oturumunu kapatır ve LoginActivity'ye yönlendirir.
     */
    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(MainActivity.this, "Başarıyla çıkış yapıldı.", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(MainActivity.this, LoginActivity.class));
        finish();
    }

    /**
     * Verilen bir metnin ilk harfini büyük harfe çevirir.
     * @param text İşlenecek metin.
     * @return İlk harfi büyük olan metin.
     */
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase(Locale.getDefault()) + text.substring(1);
    }
}