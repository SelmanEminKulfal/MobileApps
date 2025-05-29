package com.example.weatherapp;

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

public class RegisterActivity extends AppCompatActivity {

    private EditText etEmail, etPassword, etConfirmPassword;
    private Button btnRegister;
    private TextView tvLoginLink;
    private FirebaseAuth mAuth; // Firebase Authentication nesnesi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // UI elemanlarını bağlama
        etEmail = findViewById(R.id.et_register_email);
        etPassword = findViewById(R.id.et_register_password);
        etConfirmPassword = findViewById(R.id.et_register_confirm_password);
        btnRegister = findViewById(R.id.btn_register);
        tvLoginLink = findViewById(R.id.tv_login_link);

        // Firebase Authentication nesnesini başlatma
        mAuth = FirebaseAuth.getInstance();

        // Kayıt ol butonuna tıklama dinleyicisi
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Giriş yap linkine tıklama dinleyicisi
        tvLoginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish(); // RegisterActivity'yi kapat
            }
        });
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Alanların boş olup olmadığını kontrol et
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("E-posta adresi gerekli.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Şifre gerekli.");
            return;
        }
        if (TextUtils.isEmpty(confirmPassword)) {
            etConfirmPassword.setError("Şifre tekrarı gerekli.");
            return;
        }

        // Şifrelerin eşleşip eşleşmediğini kontrol et
        if (!password.equals(confirmPassword)) {
            etConfirmPassword.setError("Şifreler eşleşmiyor.");
            Toast.makeText(RegisterActivity.this, "Şifreler eşleşmiyor.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Şifre uzunluğunu kontrol et (Firebase min 6 karakter ister)
        if (password.length() < 6) {
            etPassword.setError("Şifre en az 6 karakter olmalı.");
            Toast.makeText(RegisterActivity.this, "Şifre en az 6 karakter olmalı.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Firebase ile yeni kullanıcı oluştur
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Kayıt başarılı
                            Toast.makeText(RegisterActivity.this, "Kayıt başarılı.", Toast.LENGTH_SHORT).show();
                            // Kullanıcıyı giriş ekranına yönlendir
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish(); // RegisterActivity'yi kapat
                        } else {
                            // Kayıt başarısız
                            Toast.makeText(RegisterActivity.this, "Kayıt başarısız: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}