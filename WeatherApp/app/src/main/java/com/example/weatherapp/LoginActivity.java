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
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvRegisterLink;
    private FirebaseAuth mAuth; // Firebase Authentication nesnesi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // UI elemanlarını bağlama
        etEmail = findViewById(R.id.et_login_email);
        etPassword = findViewById(R.id.et_login_password);
        btnLogin = findViewById(R.id.btn_login);
        tvRegisterLink = findViewById(R.id.tv_register_link);

        // Firebase Authentication nesnesini başlatma
        mAuth = FirebaseAuth.getInstance();

        // Eğer kullanıcı zaten giriş yapmışsa, doğrudan MainActivity'ye yönlendir
        if (mAuth.getCurrentUser() != null) {
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish(); // LoginActivity'yi kapat
        }

        // Giriş yap butonuna tıklama dinleyicisi
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });

        // Kayıt ol linkine tıklama dinleyicisi
        tvRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class));
                finish(); // LoginActivity'yi kapat
            }
        });
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Alanların boş olup olmadığını kontrol et
        if (TextUtils.isEmpty(email)) {
            etEmail.setError("E-posta adresi gerekli.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            etPassword.setError("Şifre gerekli.");
            return;
        }

        // Firebase ile giriş yap
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Giriş başarılı
                            Toast.makeText(LoginActivity.this, "Giriş başarılı.", Toast.LENGTH_SHORT).show();
                            // Kullanıcıyı ana ekrana yönlendir
                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                            finish(); // LoginActivity'yi kapat
                        } else {
                            // Giriş başarısız
                            Toast.makeText(LoginActivity.this, "Giriş başarısız: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}