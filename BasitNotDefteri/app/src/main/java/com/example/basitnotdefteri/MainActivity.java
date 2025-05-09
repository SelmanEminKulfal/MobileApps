package com.example.basitnotdefteri;

// Gerekli import'lar (öncekilere ek olarak veya güncel haliyle)
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.ItemTouchHelper; // ItemTouchHelper için import

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

// NoteItemTouchHelperCallback.OnSwipeListener arayüzünü implement et
public class MainActivity extends AppCompatActivity implements NoteItemTouchHelperCallback.OnSwipeListener {

    private RecyclerView recyclerViewNotes;
    private FloatingActionButton fabAddNote;
    private TextView textViewUserInitial;

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;

    private NoteAdapter noteAdapter;
    private List<Note> noteList;

    // Güncelleme Activity'sinden sonuç almak için Request Code
    private static final int EDIT_NOTE_REQUEST = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Firebase instance'ları alın
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Kullanıcı kontrolü (önceki koddan)
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        // Layout elemanlarını bağlama (önceki koddan)
        recyclerViewNotes = findViewById(R.id.recyclerViewNotes);
        fabAddNote = findViewById(R.id.fabAddNote);
        textViewUserInitial = findViewById(R.id.textViewUserInitial);

        // Kullanıcı baş harfini ayarlama (önceki koddan)
        String userEmail = currentUser.getEmail();
        if (userEmail != null && !userEmail.isEmpty()) {
            String initial = userEmail.substring(0, 1).toUpperCase();
            textViewUserInitial.setText(initial);
        } else {
            textViewUserInitial.setText("?");
        }

        // RecyclerView'ı ayarlama (önceki koddan)
        recyclerViewNotes.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewNotes.setHasFixedSize(true);

        // Not listesini ve adaptörü oluşturma (önceki koddan)
        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter();
        recyclerViewNotes.setAdapter(noteAdapter);

        // Adaptördeki elemanlara tıklama olayını dinleme (Güncelleme için kullanılacak)
        // RecyclerView elemanına tıklama olayını artık burada doğrudan kullanmayacağız,
        // güncelleme sağa kaydırma ile tetiklenecek. Bu listener'ı kaldırabilirsiniz
        // veya farklı bir amaçla kullanabilirsiniz (örneğin not detay ekranı).
        /*
        noteAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
               // Sağ kaydırma ile güncelleme yapacağız, bu tıklama listener'ı artık farklı bir amaçla kullanılabilir
               // editNote(noteAdapter.getNoteAt(position)); // Artık sağa kaydırma ile yapılacak
            }
        });
        */


        // FAB'a tıklama olayı ekleme (önceki koddan)
        fabAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
                // Yeni not eklerken herhangi bir veri göndermiyoruz
                startActivity(intent);
            }
        });

        // Kullanıcı baş harfi TextView'ine tıklama olayı ekleme (önceki koddan)
        textViewUserInitial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showUserOptionsDialog();
            }
        });

        // Notları Firestore'dan çekme ve dinleme işlemini başlat (önceki koddan)
        fetchNotes();

        // >>> ItemTouchHelper'ı oluştur ve RecyclerView'a bağla <<<
        // Silme ve Güncelleme ikonlarının resource ID'lerini kullan
        int deleteIconResId = R.drawable.ic_delete_white; // Oluşturduğunuz silme ikonu ID'si
        int editIconResId = R.drawable.ic_edit_white;   // Oluşturduğunuz güncelleme ikonu ID'si
        int deleteBackgroundColor = Color.RED; // Silme için kırmızı arkaplan
        int editBackgroundColor = Color.BLUE;  // Güncelleme için mavi arkaplan (veya istediğiniz renk)

        NoteItemTouchHelperCallback callback = new NoteItemTouchHelperCallback(this,
                deleteIconResId, editIconResId, deleteBackgroundColor, editBackgroundColor);
        new ItemTouchHelper(callback).attachToRecyclerView(recyclerViewNotes);
    }

    // Uygulama geri döndüğünde (AddEditNoteActivity'den gibi) not listesini yenilemek için (önceki koddan)
    @Override
    protected void onResume() {
        super.onResume();
        // Listener kullandığımız için fetchNotes() burada çağırmak zorunlu değil ama zarar vermez
        // fetchNotes();
    }

    // Notları Firestore'dan çeken ve gerçek zamanlı güncellemeleri dinleyen metot (önceki koddan)
    private void fetchNotes() {
        if (currentUser == null) return;

        CollectionReference notesCollectionRef = db.collection("notes");

        Query userNotesQuery = notesCollectionRef
                .whereEqualTo("userId", currentUser.getUid())
                .orderBy("timestamp", Query.Direction.DESCENDING);

        userNotesQuery.addSnapshotListener(this, new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@NonNull QuerySnapshot queryDocumentSnapshots, @NonNull FirebaseFirestoreException e) {
                if (e != null) {
                    Toast.makeText(MainActivity.this, "Notlar çekilirken hata oluştu: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    return;
                }

                if (queryDocumentSnapshots != null) {
                    noteList.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots.getDocuments()) {
                        Note note = documentSnapshot.toObject(Note.class);
                        if (note != null) {
                            note.setId(documentSnapshot.getId()); // Belge kimliğini Note objesine set et
                            noteList.add(note);
                        }
                    }
                    noteAdapter.setNotes(noteList); // Adaptörü güncelle
                }
            }
        });
    }

    // >>> ItemTouchHelper.SimpleCallback.OnSwipeListener metotları <<<

    // Bir öğe kaydırıldığında çağrılır
    @Override
    public void onSwipe(int position, int direction) {
        // Kaydırılan notu al
        Note swipedNote = noteAdapter.getNoteAt(position);

        if (direction == ItemTouchHelper.LEFT) {
            // Sola kaydırıldı (Silme)
            showDeleteConfirmationDialog(swipedNote, position); // Silme onayı dialogunu göster
        } else if (direction == ItemTouchHelper.RIGHT) {
            // Sağa kaydırıldı (Güncelleme)
            editNote(swipedNote); // Notu düzenleme ekranına gönder
        }
    }

    // OnSwipeListener arayüzü için Context sağlar (ikonları yüklemek için)
    @Override
    public android.content.Context getContext() {
        return this; // MainActivity Context'ini döndür
    }

    // >>> Silme ve Güncelleme Yardımcı Metotları <<<

    // Silme onayı dialogunu gösteren metot
    private void showDeleteConfirmationDialog(final Note noteToDelete, final int position) {
        new AlertDialog.Builder(this)
                .setTitle("Notu Sil")
                .setMessage("Bu notu silmek istediğinizden emin misiniz?")
                .setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Onaylandı, notu sil
                        deleteNote(noteToDelete.getId());
                    }
                })
                .setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // İptal edildi, notu geri getir
                        noteAdapter.notifyItemChanged(position);
                        dialog.cancel();
                    }
                })
                .show();
    }

    // Notu Firestore'dan silen metot
    private void deleteNote(String noteId) {
        if (noteId == null || noteId.isEmpty()) {
            Toast.makeText(this, "Silinecek not bulunamadı.", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("notes").document(noteId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Firestore'dan silme başarılı.
                        // Listeyi zaten Firestore listener'ı güncelleyecektir,
                        // bu yüzden adaptörde elle çıkarma yapmaya gerek yok.
                        Toast.makeText(MainActivity.this, "Not silindi.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Not silinirken hata oluştu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Notu düzenleme ekranına gönderen metot
    private void editNote(Note noteToEdit) {
        Intent intent = new Intent(MainActivity.this, AddEditNoteActivity.class);
        // Notun verilerini Intent'e putExtra ile ekle
        intent.putExtra("NOTE_ID", noteToEdit.getId());
        intent.putExtra("NOTE_TITLE", noteToEdit.getTitle());
        intent.putExtra("NOTE_CONTENT", noteToEdit.getContent());

        // startActivity ile düzenleme ekranını başlat
        // Düzenleme ekranından bir sonuç bekliyorsak startActivityForResult kullanmalıyız
        startActivityForResult(intent, EDIT_NOTE_REQUEST);

        // TODO: Eğer düzenleme Activity'sinden kaydetme sonucu almak ve
        // MainActivity'de buna göre aksiyon almak isterseniz (örneğin Toast göstermek),
        // startActivity(intent) yerine startActivityForResult(intent, EDIT_NOTE_REQUEST);
        // kullanmalı ve onActivityResult metodunu override etmelisiniz.
    }

    // >>> Kullanıcı Yönetimi Metotları (Önceki Koddan) <<<

    // Kullanıcı seçenekleri dialogunu gösteren metot (Değişiklik yok)
    private void showUserOptionsDialog() {
        // ... (Yukarıdaki kodun aynısı)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Kullanıcı Seçenekleri");
        String[] options = {"Şifreyi Güncelle", "Çıkış Yap", "Hesabı Sil"};
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0: // Şifreyi Güncelle
                        showChangePasswordDialog();
                        break;
                    case 1: // Çıkış Yap
                        logoutUser();
                        break;
                    case 2: // Hesabı Sil
                        showDeleteAccountDialog();
                        break;
                }
            }
        });
        builder.show();
    }
    // Kullanıcı çıkışı (Logout) metodu (Değişiklik yok)
    private void logoutUser() {
        mAuth.signOut();
        Toast.makeText(this, "Çıkış yapıldı.", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
    // Şifre Güncelleme Dialogunu Gösteren Metot (Değişiklik yok)
    private void showChangePasswordDialog() {
        // ... (Yukarıdaki kodun aynısı)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Şifreyi Güncelle");
        final EditText newPasswordEditText = new EditText(this);
        newPasswordEditText.setHint("Yeni Şifre");
        newPasswordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(newPasswordEditText);

        builder.setPositiveButton("Güncelle", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPassword = newPasswordEditText.getText().toString().trim();
                if (TextUtils.isEmpty(newPassword)) {
                    Toast.makeText(MainActivity.this, "Yeni şifre boş olamaz.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (currentUser != null) {
                    currentUser.updatePassword(newPassword)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(MainActivity.this, "Şifre başarıyla güncellendi.", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Şifre güncellenemedi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }
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

    // Hesap Silme Dialogunu Gösteren Metot (Değişiklik yok)
    private void showDeleteAccountDialog() {
        // ... (Yukarıdaki kodun aynısı)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hesabı Sil");
        builder.setMessage("Bu işlem geri alınamaz. Hesabınızı kalıcı olarak silmek istediğinizden emin misiniz?");

        builder.setPositiveButton("Evet, Sil", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showReauthenticateAndDeleteDialog();
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

    // Hesap silme öncesi yeniden kimlik doğrulama dialogunu gösteren metot (Değişiklik yok)
    private void showReauthenticateAndDeleteDialog() {
        // ... (Yukarıdaki kodun aynısı)
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Hesabı Silmek İçin Kimlik Doğrulama");
        builder.setMessage("Lütfen güvenliğiniz için mevcut şifrenizi girin.");

        final EditText passwordEditText = new EditText(this);
        passwordEditText.setHint("Mevcut Şifre");
        passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        builder.setView(passwordEditText);

        builder.setPositiveButton("Onayla", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String currentPassword = passwordEditText.getText().toString().trim();

                if (TextUtils.isEmpty(currentPassword)) {
                    Toast.makeText(MainActivity.this, "Mevcut şifre boş olamaz.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (currentUser != null && currentUser.getEmail() != null) { // E-posta null kontrolü eklendi
                    AuthCredential credential = EmailAuthProvider.getCredential(currentUser.getEmail(), currentPassword);

                    currentUser.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        deleteAuthenticatedAccount();
                                    } else {
                                        Toast.makeText(MainActivity.this, "Kimlik doğrulama başarısız. Şifrenizi kontrol edin.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(MainActivity.this, "Kullanıcı bilgisi alınamadı.", Toast.LENGTH_SHORT).show();
                }
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

    // Yeniden kimlik doğrulama yapıldıktan sonra hesabı silen metot (Değişiklik yok)
    private void deleteAuthenticatedAccount() {
        if (currentUser != null) { // currentUser null kontrolü eklendi
            currentUser.delete()
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Hesap başarıyla silindi.", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(MainActivity.this, "Hesap silinemedi: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        }
    }
}