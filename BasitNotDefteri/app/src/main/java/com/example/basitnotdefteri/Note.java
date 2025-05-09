package com.example.basitnotdefteri;

import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class Note {

    private String id; // Firestore belge kimliği için (isteğe bağlı olarak burada tutulabilir)
    private String title;
    private String content;
    private String userId; // Notun hangi kullanıcıya ait olduğunu belirlemek için
    private Date timestamp; // Notun oluşturulma zamanı (Firestore server timestamp)

    // Firestore için boş constructor gereklidir
    public Note() {
        // Boş constructor
    }

    // Not oluştururken kullanılacak constructor
    public Note(String title, String content, String userId) {
        this.title = title;
        this.content = content;
        this.userId = userId;
        // Timestamp Firestore tarafından otomatik doldurulacak
    }

    // --- Getter ve Setter Metotları ---

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @ServerTimestamp // Firestore'un bu alanı otomatik olarak doldurmasını sağlar
    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

