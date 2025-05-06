package com.example.weatherapp; // Kendi paket adınızla değiştirin

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent; // Başka bir Activity'ye geçmek için
import android.os.Bundle;
import android.text.TextUtils; // Metin kontrolleri için
import android.util.Log; // Loglama için
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth; // Firebase Authentication sınıfı

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity"; // Loglar için TAG

    private EditText editTextRegEmail;
    private EditText editTextRegPassword;
    private EditText editTextRegConfirmPassword;
    private Button buttonRegister;

    private FirebaseAuth mAuth; // Firebase Authentication nesnesi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // activity_register.xml layout dosyasını kullan

        // Firebase Authentication nesnesini al
        mAuth = FirebaseAuth.getInstance();

        // Layout dosyasındaki bileşenleri bul
        editTextRegEmail = findViewById(R.id.editTextRegEmail);
        editTextRegPassword = findViewById(R.id.editTextRegPassword);
        editTextRegConfirmPassword = findViewById(R.id.editTextRegConfirmPassword);
        buttonRegister = findViewById(R.id.buttonRegister);

        // Kayıt Ol butonuna tıklanma olayını dinle
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser(); // Kayıt işlemini başlatan metodu çağır
            }
        });
    }

    // Kullanıcı kayıt işlemini gerçekleştiren metod
    private void registerUser() {
        String email = editTextRegEmail.getText().toString().trim(); // E-posta alanındaki metni al ve boşlukları temizle
        String password = editTextRegPassword.getText().toString().trim(); // Şifre alanındaki metni al ve boşlukları temizle
        String confirmPassword = editTextRegConfirmPassword.getText().toString().trim(); // Şifre tekrar alanındaki metni al ve boşlukları temizle

        // Alanların boş olup olmadığını kontrol et
        if (TextUtils.isEmpty(email)) {
            editTextRegEmail.setError("E-posta boş olamaz.");
            return; // Metoddan çık
        }
        if (TextUtils.isEmpty(password)) {
            editTextRegPassword.setError("Şifre boş olamaz.");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            editTextRegConfirmPassword.setError("Şifre tekrar boş olamaz.");
            return;
        }

        // Şifrelerin eşleşip eşleşmediğini kontrol et
        if (!password.equals(confirmPassword)) {
            editTextRegConfirmPassword.setError("Şifreler eşleşmiyor.");
            Toast.makeText(this, "Şifreler eşleşmiyor.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Şifrenin minimum uzunluğunu kontrol et (Firebase varsayılan olarak 6 karakter ister)
        if (password.length() < 6) {
            editTextRegPassword.setError("Şifre en az 6 karakter olmalıdır.");
            Toast.makeText(this, "Şifre en az 6 karakter olmalıdır.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase Authentication ile yeni kullanıcı oluştur
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Kayıt başarılı
                            Log.d(TAG, "createUserWithEmail:success");
                            // Firebase konsolunda yeni kullanıcıyı görebilirsiniz.
                            Toast.makeText(RegisterActivity.this, "Kayıt başarılı.",
                                    Toast.LENGTH_SHORT).show();

                            // Kayıt başarılı olduktan sonra Giriş sayfasına yönlendir
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish(); // Kayıt Activity'sini kapat
                        } else {
                            // Kayıt başarısız
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            // Hata mesajını kullanıcıya göster
                            Toast.makeText(RegisterActivity.this, "Kayıt başarısız: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}