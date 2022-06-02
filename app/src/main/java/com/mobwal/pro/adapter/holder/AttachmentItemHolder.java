package com.mobwal.pro.adapter.holder;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import com.mobwal.pro.R;
import com.mobwal.pro.models.db.Attachment;
import com.mobwal.pro.ui.RecycleViewItemListeners;
import com.mobwal.android.library.SimpleFileManager;

/**
 * Элемент галереи
 */
public class AttachmentItemHolder extends RecyclerView.ViewHolder {

    private final ImageView mImage;
    private final SimpleFileManager mFileManager;
    private final Context mContext;
    private Attachment mAttachment;
    private final RecycleViewItemListeners mListeners;

    public AttachmentItemHolder(@NonNull View itemView, @Nullable RecycleViewItemListeners listeners) {
        super(itemView);

        mListeners = listeners;
        mContext = itemView.getContext();

        mFileManager = new SimpleFileManager(itemView.getContext().getFilesDir());

        mImage = itemView.findViewById(R.id.attach_item_image);
        ImageButton trash = itemView.findViewById(R.id.attach_item_trash);

        if(mListeners != null) {
            mImage.setOnClickListener(v -> mListeners.onViewItemClick(mAttachment.id));
            trash.setOnClickListener(v-> mListeners.onViewItemInfo(mAttachment.id));
        }
    }

    public void bind(@NotNull Attachment item) {
        mAttachment = item;

        /*try {
            byte[] bytes = mFileManager.readPath(item.f_route, item.c_name);
            if(bytes != null) {
                Bitmap bitmap = BitmapCache.getBitmap(item.c_name, bytes, 80);
                mImage.setImageBitmap(bitmap);
            } else {
                mImage.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.ic_attach_empty_96));
            }
        } catch (IOException e) {
            WalkerApplication.Log("Ошибка сжатия изображения для галереи.", e);
        }*/
    }
}
