package com.example.todoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements TaskAdapter.OnTaskActionListener {

    private EditText editTextTask;
    private Button buttonAdd;
    private Button buttonSort; // Yeni eklenen buton
    private RecyclerView recyclerViewTasks;
    private DatabaseHelper dbHelper;
    private TaskAdapter taskAdapter;
    private List<Task> taskList;
    private Switch switchHideCompleted;
    private boolean isSortedAsc = false; // Sıralama durumunu takip eden değişken

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // UI bileşenlerini bağla
        editTextTask = findViewById(R.id.editTextTask);
        buttonAdd = findViewById(R.id.buttonAdd);
        buttonSort = findViewById(R.id.buttonSort); // Butonu bağla
        recyclerViewTasks = findViewById(R.id.recyclerViewTasks);
        switchHideCompleted = findViewById(R.id.switchHideCompleted);
        if (switchHideCompleted != null) {
            // Switch'in durum değişikliğini dinle
            switchHideCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
                loadTasks(); // Durum değiştiğinde listeyi yeniden yükle
            });
        }

        // Veritabanı ve liste ayarları
        dbHelper = new DatabaseHelper(this);
        taskList = new ArrayList<>();
        taskAdapter = new TaskAdapter(taskList, this);

        recyclerViewTasks.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTasks.setAdapter(taskAdapter);

        // Görevleri veritabanından çek ve listeyi doldur
        loadTasks();

        // Ekle butonuna tıklama dinleyicisi
        buttonAdd.setOnClickListener(v -> {
            String taskTitle = editTextTask.getText().toString().trim();
            if (!taskTitle.isEmpty()) {
                addTask(taskTitle);
            } else {
                Toast.makeText(MainActivity.this, "Lütfen bir görev girin.", Toast.LENGTH_SHORT).show();
            }
        });

        // Switch'in durum değişikliğini dinle
        switchHideCompleted.setOnCheckedChangeListener((buttonView, isChecked) -> {
            loadTasks(); // Durum değiştiğinde listeyi yeniden yükle
        });

        // Sıralama butonuna tıklama dinleyicisi
        buttonSort.setOnClickListener(v -> {
            isSortedAsc = !isSortedAsc; // Sıralama durumunu tersine çevir
            loadTasks(); // Listeyi yeniden yükle
        });
    }

    // Görev ekleme metodu
    private void addTask(String title) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TASK_TITLE, title);

        long newRowId = db.insert(DatabaseHelper.TABLE_TASKS, null, values);

        if (newRowId != -1) {
            Toast.makeText(this, "Görev başarıyla eklendi.", Toast.LENGTH_SHORT).show();
            editTextTask.setText("");
            loadTasks();
        } else {
            Toast.makeText(this, "Hata: Görev eklenemedi.", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    // Görevleri veritabanından okuma metodu
    private void loadTasks() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = null;
        String[] selectionArgs = null;
        String sortOrder = null; // Yeni eklenen sıralama değişkeni

        // Tamamlananları gizle seçiliyse filtreleme uygula
        if (switchHideCompleted.isChecked()) {
            selection = DatabaseHelper.COLUMN_IS_COMPLETED + " = ?";
            selectionArgs = new String[]{"0"};
        }

        // Sıralama durumu açıksa ORDER BY koşulunu ekle
        if (isSortedAsc) {
            sortOrder = DatabaseHelper.COLUMN_TASK_TITLE + " ASC";
        }

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_TASKS,
                null,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder // Sıralama parametresini kullan
        );

        taskList.clear();
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_ID));
            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TASK_TITLE));
            boolean isCompleted = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_IS_COMPLETED)) == 1;

            taskList.add(new Task(id, title, isCompleted));
        }
        cursor.close();
        db.close();
        taskAdapter.updateTasks(taskList);
    }

    // Silme butonu tıklama dinleyicisinin implementasyonu
    @Override
    public void onDeleteClick(int taskId) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(taskId) };
        int deletedRows = db.delete(DatabaseHelper.TABLE_TASKS, selection, selectionArgs);

        if (deletedRows > 0) {
            Toast.makeText(this, "Görev silindi.", Toast.LENGTH_SHORT).show();
            loadTasks();
        } else {
            Toast.makeText(this, "Hata: Görev silinemedi.", Toast.LENGTH_SHORT).show();
        }
        db.close();
    }

    // Tamamlanma durumu değiştiğinde çağrılan metot
    @Override
    public void onStatusChange(int taskId, boolean isCompleted) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_IS_COMPLETED, isCompleted ? 1 : 0);

        String selection = DatabaseHelper.COLUMN_ID + " = ?";
        String[] selectionArgs = { String.valueOf(taskId) };

        int updatedRows = db.update(
                DatabaseHelper.TABLE_TASKS,
                values,
                selection,
                selectionArgs
        );

        if (updatedRows > 0) {
            loadTasks();
        }
        db.close();
    }
}