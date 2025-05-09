package com.example.basitnotdefteri; // Paket adınız

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent; // Intent için import
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference; // Belge referansı için import

public class AddEditNoteActivity extends AppCompatActivity {

    private EditText editTextNoteTitle, editTextNoteContent;
    private Button buttonSaveNote;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    // Düzenleme modundaysak güncellenecek notun ID'sini tutacak değişken
    private String noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        // Firestore ve Auth instance'larını alın
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(this, "Giriş yapmanız gerekiyor.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // Layout elemanlarını id'leri ile bağlama
        editTextNoteTitle = findViewById(R.id.editTextNoteTitle);
        editTextNoteContent = findViewById(R.id.editTextNoteContent);
        buttonSaveNote = findViewById(R.id.buttonSaveNote);

        // >>> Intent ile gelen not verilerini kontrol et ve ekranı doldur <<<
        Intent intent = getIntent();
        // Eğer Intent'te "NOTE_ID" extra'sı varsa, düzenleme modundayız
        if (intent.hasExtra("NOTE_ID")) {
            // Düzenleme modunda
            setTitle("Notu Düzenle"); // Activity başlığını değiştir
            noteId = intent.getStringExtra("NOTE_ID"); // Not ID'sini sakla
            editTextNoteTitle.setText(intent.getStringExtra("NOTE_TITLE")); // Başlığı doldur
            editTextNoteContent.setText(intent.getStringExtra("NOTE_CONTENT")); // İçeriği doldur
            buttonSaveNote.setText("Notu Güncelle"); // Buton metnini değiştir (isteğe bağlı)
        } else {
            // Yeni not ekleme modunda
            setTitle("Yeni Not Ekle"); // Activity başlığını ayarla
            // noteId değişkeni zaten null olacak, yeni not için ID Firestore tarafından atanacak
            buttonSaveNote.setText("Notu Kaydet"); // Buton metnini ayarla (isteğe bağlı)
        }


        // Kaydet butonuna tıklama olayı ekleme
        buttonSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote(); // Notu kaydetme/güncelleme metodu
            }
        });
    }

    // Notu Firestore'a kaydeden veya güncelleyen metot
    private void saveNote() {
        String title = editTextNoteTitle.getText().toString().trim();
        String content = editTextNoteContent.getText().toString().trim();

        // Başlık veya içerik boşsa uyarı ver
        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(content)) {
            Toast.makeText(this, "Başlık ve içerik boş olamaz", Toast.LENGTH_SHORT).show();
            return;
        }

        // Mevcut kullanıcı ID'sini al (onCreate'de kontrol ettik)
        String userId = currentUser.getUid();

        // Yeni veya güncellenmiş bir Not nesnesi oluştur
        Note note = new Note(title, content, userId);
        // Timestamp alanı Note modelinde @ServerTimestamp ile işaretlendiği için burada elle set etmeye gerek yok

        // >>> Firestore'a kaydetme veya güncelleme işlemi <<<
        if (noteId != null) {
            // Düzenleme modunda: Mevcut belgeyi güncelle
            db.collection("notes").document(noteId)
                    .set(note) // set() metodu belgeyi oluşturur veya varsa üzerine yazar
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            // Güncelleme başarılı
                            Toast.makeText(AddEditNoteActivity.this, "Not güncellendi!", Toast.LENGTH_SHORT).show();
                            finish(); // Güncelleme başarılıysa bu Activity'yi kapat
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Güncelleme başarısız
                            Toast.makeText(AddEditNoteActivity.this, "Not güncellenirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Yeni not ekleme modunda: Yeni belge oluştur
            db.collection("notes")
                    .add(note) // add() metodu otomatik olarak benzersiz bir belge kimliği oluşturur
                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                        @Override
                        public void onSuccess(DocumentReference documentReference) {
                            // Kayıt başarılı
                            Toast.makeText(AddEditNoteActivity.this, "Not kaydedildi!", Toast.LENGTH_SHORT).show();
                            finish(); // Kayıt başarılıysa bu Activity'yi kapat
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Kayıt başarısız
                            Toast.makeText(AddEditNoteActivity.this, "Not kaydedilirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    // Geri tuşuna basıldığında (isteğe bağlı olarak)
    @Override
    public void onBackPressed() {
        // Kullanıcı geri tuşuna bastığında ne yapılacağını belirleyebilirsiniz.
        // Örneğin, değişiklikler kaydedilmediyse uyarı verebilirsiniz.
        super.onBackPressed(); // Varsayılan geri gitme davranışını uygula
    }
}