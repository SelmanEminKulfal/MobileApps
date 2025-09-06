package com.example.todoapp;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    private List<Task> tasks;
    private OnTaskActionListener listener;

    // Arayüzleri birleştirdik
    public interface OnTaskActionListener {
        void onDeleteClick(int taskId);
        void onStatusChange(int taskId, boolean isCompleted);
    }

    public TaskAdapter(List<Task> tasks, OnTaskActionListener listener) {
        this.tasks = tasks;
        this.listener = listener;
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        Task currentTask = tasks.get(position);
        holder.textViewTaskTitle.setText(currentTask.getTitle());
        holder.checkBoxTask.setChecked(currentTask.isCompleted());

        // Tamamlanma durumuna göre yazının üzerini çiz
        if (currentTask.isCompleted()) {
            holder.textViewTaskTitle.setPaintFlags(holder.textViewTaskTitle.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        } else {
            holder.textViewTaskTitle.setPaintFlags(holder.textViewTaskTitle.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
        }

        // Silme butonuna tıklama dinleyicisi ekle
        holder.imageButtonDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDeleteClick(currentTask.getId());
            }
        });

        // Onay kutusuna tıklama dinleyicisi ekle
        holder.checkBoxTask.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null) {
                listener.onStatusChange(currentTask.getId(), isChecked);
            }
        });
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public void updateTasks(List<Task> newTasks) {
        this.tasks = newTasks;
        notifyDataSetChanged();
    }

    public static class TaskViewHolder extends RecyclerView.ViewHolder {
        public CheckBox checkBoxTask;
        public TextView textViewTaskTitle;
        public ImageButton imageButtonDelete;

        public TaskViewHolder(View itemView) {
            super(itemView);
            checkBoxTask = itemView.findViewById(R.id.checkBoxTask);
            textViewTaskTitle = itemView.findViewById(R.id.textViewTaskTitle);
            imageButtonDelete = itemView.findViewById(R.id.imageButtonDelete);
        }
    }
}