package com.example.basitnotdefteri; // Paket adınızı buraya yazın

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegister;
    private FirebaseAuth mAuth; // Firebase Authentication nesnesi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login); // Oluşturduğumuz layout'u bağlıyoruz

        // Firebase Authentication instance alın
        mAuth = FirebaseAuth.getInstance();

        // Uygulama açıldığında kullanıcının zaten giriş yapıp yapmadığını kontrol et
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Kullanıcı zaten giriş yapmış, doğrudan Ana Aktivite'ye git
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Login ekranını kapat
            return; // onCreate metodunun geri kalanını çalıştırma
        }

        // Layout elemanlarını id'leri ile bağlama
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegister = findViewById(R.id.textViewRegister); // Kayıt ol TextView'i

        // Giriş yap butonuna tıklama olayı ekleme
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser(); // Kullanıcı giriş işlemini başlatan metot
            }
        });

        // Kayıt ol TextView'ine tıklama olayı ekleme (Register ekranına gitmek için)
        textViewRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                // İsteğe bağlı: Login ekranını kapatmak için finish();
                // Ancak geri tuşu ile dönme ihtimaline karşı kapatmamak daha iyi olabilir.
            }
        });
    }

    // Kullanıcı giriş işlemini gerçekleştiren metot
    private void loginUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Alanların boş olup olmadığını kontrol etme
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("E-posta giriniz!");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Şifre giriniz!");
            return;
        }

        // Firebase ile kullanıcı girişi yapma
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Giriş başarılı
                            Toast.makeText(getApplicationContext(), "Giriş başarılı!", Toast.LENGTH_LONG).show();

                            // Giriş başarılıysa Ana Aktivite'ye git
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Login ekranını kapat
                        } else {
                            // Giriş başarısız
                            // Hata mesajını kullanıcıya göster
                            Toast.makeText(getApplicationContext(), "Giriş başarısız! Lütfen bilgilerinizi kontrol edin. Hata: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}