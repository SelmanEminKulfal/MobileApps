package com.example.basitnotdefteri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

// NoteAdapter sınıfı, RecyclerView.Adapter'dan miras alır
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    // Gösterilecek notların listesi
    private List<Note> notes = new ArrayList<>();
    // Liste elemanına tıklama olayları için listener arayüzü
    private OnItemClickListener listener;

    // ViewHolder sınıfı, her bir liste elemanının view'larını tutar
    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        public TextView textViewNoteTitle;
        public TextView textViewNoteContent;

        // ViewHolder constructor'ı, liste elemanının layout'unu alır
        public NoteViewHolder(@NonNull View itemView, final OnItemClickListener listener) {
            super(itemView);
            // Layout'taki view'ları id'leri ile bağlama
            textViewNoteTitle = itemView.findViewById(R.id.textViewNoteTitle);
            textViewNoteContent = itemView.findViewById(R.id.textViewNoteContent);

            // Liste elemanına tıklama olayını dinleme
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        // Tıklanan elemanın pozisyonunu al
                        int position = getAdapterPosition();
                        // Geçerli bir pozisyon ve listener varsa tıklama olayını tetikle
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });

            // TODO: Kaydırma (swipe) için gerekli olan view'ları burada tanımlayacağız (ileride)
        }
    }

    // RecyclerView her bir liste elemanı için ViewHolder oluşturması gerektiğinde bu metot çağrılır
    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // note_list_item layout dosyasını inflate ederek bir View oluştur
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list_item, parent, false);
        // Oluşturulan View ile bir NoteViewHolder oluştur ve döndür
        return new NoteViewHolder(itemView, listener);
    }

    // RecyclerView her bir liste elemanını göstermesi gerektiğinde bu metot çağrılır
    // Belirtilen pozisyondaki veriyi (Note objesi) alıp ViewHolder'daki view'lara bağlarız
    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note currentNote = notes.get(position);
        holder.textViewNoteTitle.setText(currentNote.getTitle());
        holder.textViewNoteContent.setText(currentNote.getContent());
        // TODO: Tarih/Saat göstermek isterseniz timestamp'i formatlayıp bir TextView'e set edebilirsiniz
    }

    // Adaptördeki toplam eleman sayısını döndürür
    @Override
    public int getItemCount() {
        return notes.size();
    }

    // Not listesini güncellemek için metot
    public void setNotes(List<Note> notes) {
        this.notes = notes;
        // Veriler değiştiğinde RecyclerView'ı bilgilendir
        notifyDataSetChanged();
        // TODO: Daha verimli güncellemeler için DiffUtil kullanılabilir
    }

    // Belirli bir pozisyondaki Note objesini döndürür (Silme/Güncelleme için kullanılacak)
    public Note getNoteAt(int position) {
        return notes.get(position);
    }

    // Liste elemanına tıklama olayları için arayüz (listener)
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Listener'ı ayarlamak için metot
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    // TODO: Kaydırma (swipe) ile silme ve güncelleme için yardımcı metotlar buraya eklenecek
}