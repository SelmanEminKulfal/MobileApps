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

public class RegisterActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonRegister;
    private TextView textViewLogin;
    private FirebaseAuth mAuth; // Firebase Authentication nesnesi

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register); // Oluşturduğumuz layout'u bağlıyoruz

        // Firebase Authentication instance alın
        mAuth = FirebaseAuth.getInstance();

        // Layout elemanlarını id'leri ile bağlama
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonRegister = findViewById(R.id.buttonRegister);
        textViewLogin = findViewById(R.id.textViewLogin); // Giriş yap TextView'i

        // Kayıt ol butonuna tıklama olayı ekleme
        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerNewUser(); // Kullanıcı kayıt işlemini başlatan metot
            }
        });

        // Giriş yap TextView'ine tıklama olayı ekleme (Login ekranına gitmek için)
        textViewLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                // İsteğe bağlı: Kayıt ekranını kapatmak için finish();
            }
        });
    }

    // Yeni kullanıcı kayıt işlemini gerçekleştiren metot
    private void registerNewUser() {
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

        // Firebase ile kullanıcı oluşturma
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Kayıt başarılı
                            Toast.makeText(getApplicationContext(), "Kayıt başarılı!", Toast.LENGTH_LONG).show();

                            // Kayıt başarılıysa Login ekranına git
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish(); // Kayıt ekranını kapat
                        } else {
                            // Kayıt başarısız
                            // Hata mesajını kullanıcıya göster
                            Toast.makeText(getApplicationContext(), "Kayıt başarısız! Lütfen tekrar deneyin. Hata: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}