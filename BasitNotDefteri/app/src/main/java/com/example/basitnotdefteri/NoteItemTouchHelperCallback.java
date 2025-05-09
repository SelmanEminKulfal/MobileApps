package com.example.basitnotdefteri;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

// Kaydırma olaylarını yönetmek için ItemTouchHelper.SimpleCallback'den miras alıyoruz
public class NoteItemTouchHelperCallback extends ItemTouchHelper.SimpleCallback {

    // Kaydırma olaylarını MainActivity'de işlemek için bir Listener arayüzü
    private OnSwipeListener listener;

    // Silme ve Güncelleme için arkaplan ve ikon Drawable'ları
    private Drawable deleteIcon;
    private Drawable editIcon;
    private final ColorDrawable backgroundDelete;
    private final ColorDrawable backgroundEdit;


    // Constructor, kaydırma yönlerini ve listener'ı ayarlar
    public NoteItemTouchHelperCallback(OnSwipeListener listener, int deleteIconResId, int editIconResId, int deleteBackgroundColor, int editBackgroundColor) {
        // Sürüklemeyi devre dışı bırak (0), sola kaydırmayı (LEFT) ve sağa kaydırmayı (RIGHT) etkinleştir
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        this.listener = listener;

        // Silme ve Güncelleme ikonlarını yükle
        deleteIcon = ContextCompat.getDrawable(listener.getContext(), deleteIconResId);
        editIcon = ContextCompat.getDrawable(listener.getContext(), editIconResId);

        // Silme ve Güncelleme arkaplan renklerini oluştur
        backgroundDelete = new ColorDrawable(deleteBackgroundColor);
        backgroundEdit = new ColorDrawable(editBackgroundColor);
    }

    // Sürükleme ve bırakma olayları için (Not defterinde gerekli değil)
    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        return false; // Sürüklemeyi devre dışı bırak
    }

    // Bir öğe kaydırıldığında çağrılır
    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        // Kaydırılan öğenin pozisyonunu al
        int position = viewHolder.getAdapterPosition();

        // Listener'ı tetikle ve hangi yöne kaydırıldığını bildir
        if (listener != null) {
            listener.onSwipe(position, direction);
        }

        // NOT: onSwiped çağrıldığında RecyclerView öğeyi listeden kaldırır.
        // Eğer silme işlemi onay gerektiriyorsa, onay gelene kadar öğeyi geri getirmemiz gerekir.
        // Bu, onSwipeListener içinde handledialogs() gibi metotlarda yapılır.
        // Dialog açıldığında notifyItemChanged(position) çağırılarak öğe geri getirilir.
    }

    // Kaydırma sırasında görsel geri bildirim sağlamak için çağrılır (Arkaplan ve ikon çizimi)
    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        View itemView = viewHolder.itemView;
        int itemHeight = itemView.getHeight();
        int intrinsicWidthDelete = deleteIcon.getIntrinsicWidth();
        int intrinsicHeightDelete = deleteIcon.getIntrinsicHeight();
        int intrinsicWidthEdit = editIcon.getIntrinsicWidth();
        int intrinsicHeightEdit = editIcon.getIntrinsicHeight();

        // Sola kaydırma (Silme)
        if (dX > 0) { // Sağa doğru kaydırıldı (Güncelleme)
            int editIconMargin = (itemHeight - intrinsicHeightEdit) / 2;
            int editIconTop = itemView.getTop() + (itemHeight - intrinsicHeightEdit) / 2;
            int editIconBottom = editIconTop + intrinsicHeightEdit;
            int editIconLeft = itemView.getLeft() + editIconMargin;
            int editIconRight = itemView.getLeft() + editIconMargin + intrinsicWidthEdit;
            editIcon.setBounds(editIconLeft, editIconTop, editIconRight, editIconBottom);

            backgroundEdit.setBounds(itemView.getLeft(), itemView.getTop(),
                    itemView.getLeft() + ((int) dX), itemView.getBottom());
            backgroundEdit.draw(c);
            editIcon.draw(c);

        } else if (dX < 0) { // Sola doğru kaydırıldı (Silme)
            int deleteIconMargin = (itemHeight - intrinsicHeightDelete) / 2;
            int deleteIconTop = itemView.getTop() + (itemHeight - intrinsicHeightDelete) / 2;
            int deleteIconBottom = deleteIconTop + intrinsicHeightDelete;
            int deleteIconLeft = itemView.getRight() - deleteIconMargin - intrinsicWidthDelete;
            int deleteIconRight = itemView.getRight() - deleteIconMargin;
            deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom);

            backgroundDelete.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(),
                    itemView.getRight(), itemView.getBottom());
            backgroundDelete.draw(c);
            deleteIcon.draw(c);
        } else { // Kaydırma yok veya sıfırlandı
            backgroundDelete.setBounds(0,0,0,0); // Arka planı sıfırla
            backgroundEdit.setBounds(0,0,0,0); // Arka planı sıfırla
        }


        // Varsayılan kaydırma davranışını çağır
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }


    // Kaydırma olaylarını MainActivity'ye bildirmek için arayüz
    public interface OnSwipeListener {
        void onSwipe(int position, int direction);
        android.content.Context getContext(); // İkonları yüklemek için context sağlamak
    }
}
