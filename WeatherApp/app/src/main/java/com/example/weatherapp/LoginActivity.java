package com.example.weatherapp; // Kendi paket adınızla değiştirin

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser; // FirebaseUser sınıfı

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogin;
    private TextView textViewRegisterLink;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Firebase Authentication nesnesini al
        mAuth = FirebaseAuth.getInstance();

        // Bileşenleri bul
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        buttonLogin = findViewById(R.id.buttonLogin);
        textViewRegisterLink = findViewById(R.id.textViewRegisterLink);

        // "Hesabın yok mu? Kayıt ol" yazısına tıklanma olayını dinle (Mevcut kod)
        textViewRegisterLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        // Giriş Yap butonuna tıklanma olayını dinle (Mevcut kod)
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser(); // Giriş yapma metodunu çağır
            }
        });

        // onCreate'de otomatik kontrol yapmıyoruz, onStart'ta yapacağız.
        // checkCurrentUser(); // Bu çağrıyı silebilirsiniz veya yorum satırı yapabilirsiniz
    }

    // Activity görünür olduğunda çağrılır. Kullanıcı oturumunu burada kontrol et.
    @Override
    public void onStart() {
        super.onStart();
        // Kullanıcının oturum açmış olup olmadığını kontrol et (null değilse oturum açık).
        FirebaseUser currentUser = mAuth.getCurrentUser(); // Şu anki kullanıcıyı al

        if (currentUser != null) {
            // Kullanıcı zaten giriş yapmışsa, doğrudan Ana (Hava Durumu) sayfasına git
            Log.d(TAG, "onStart: User already signed in.");
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Login Activity'sini kapat
        } else {
            // Kullanıcı oturumu yoksa, Giriş Yap ekranını göster
            Log.d(TAG, "onStart: No user signed in.");
            // Herhangi bir şey yapmaya gerek yok, Login ekranı zaten görünüyor olacak
        }
    }


    // Kullanıcı giriş işlemini gerçekleştiren metod (Mevcut kod)
    private void signInUser() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("E-posta boş olamaz.");
            return;
        }
        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Şifre boş olamaz.");
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithEmail:success");
                            // FirebaseUser user = mAuth.getCurrentUser(); // İstersen burada da alabilirsin

                            Toast.makeText(LoginActivity.this, "Giriş başarılı.",
                                    Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Giriş başarısız: " + task.getException().getMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }


}