package com.mobwal.pro.adapter.holder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import com.mobwal.android.library.LogManager;
import com.mobwal.android.library.NewThread;
import com.mobwal.android.library.authorization.BasicAuthorizationSingleton;
import com.mobwal.android.library.util.ImageUtil;
import com.mobwal.android.library.util.StreamUtil;
import com.mobwal.pro.Names;
import com.mobwal.pro.R;
import com.mobwal.pro.WalkerApplication;
import com.mobwal.pro.models.db.Attachment;
import com.mobwal.pro.ui.RecycleViewItemListeners;
import com.mobwal.android.library.SimpleFileManager;

import java.io.IOException;

/**
 * Элемент галереи
 */
public class AttachmentItemHolder extends RecyclerView.ViewHolder {

    private final ImageView mImage;
    private final ImageButton mTrash;
    private final SimpleFileManager mFileManager;
    private final Context mContext;
    private Attachment mAttachment;
    private final RecycleViewItemListeners mListeners;

    public AttachmentItemHolder(@NonNull View itemView, @Nullable RecycleViewItemListeners listeners) {
        super(itemView);

        mListeners = listeners;
        mContext = itemView.getContext();

        mFileManager = new SimpleFileManager(itemView.getContext().getFilesDir(),
                BasicAuthorizationSingleton.getInstance().getUser().getCredential());

        mImage = itemView.findViewById(R.id.attach_item_image);
        mTrash = itemView.findViewById(R.id.attach_item_trash);

        if(mListeners != null) {
            mImage.setOnClickListener(v -> mListeners.onViewItemClick(mAttachment.id));
            mTrash.setOnClickListener(v-> mListeners.onViewItemInfo(mAttachment.id));
        }
    }

    public void bind(@NotNull Attachment item) {
        mAttachment = item;

        mTrash.setVisibility(item.b_disabled ? View.GONE : View.VISIBLE);

        if(item.b_server) {
            NewThread newThread = new NewThread((Activity)mContext) {
                Bitmap mBitmap;
                @Override
                public void onBackgroundExecute() {
                    try {
                        mBitmap = WalkerApplication.getBitmap(item.id);
                        if(mBitmap == null) {
                            byte[] bytes = StreamUtil.readURL(Names.getConnectUrl() + "/file/" + item.id, 5000);
                            mBitmap = WalkerApplication.getBitmap(item.id, bytes, 120);
                        }
                    } catch (IOException e) {
                        LogManager.getInstance().error("Ошибка загрузки изображения с сервера. ", e);
                    }
                }

                @Override
                public void onPostExecute() {
                    mImage.setImageBitmap(item.b_disabled ? ImageUtil.blur(mContext, mBitmap) : mBitmap);
                }
            };

            newThread.run();
        } else {
            try {
                byte[] bytes = mFileManager.readPath(item.c_name);
                if (bytes != null) {
                    Bitmap bitmap = ImageUtil.getSizedBitmap(bytes, 0, bytes.length, 80);
                    mImage.setImageBitmap(bitmap);
                } else {
                    mImage.setImageDrawable(AppCompatResources.getDrawable(mContext, R.drawable.ic_attach_empty_96));
                }
            } catch (IOException e) {
                LogManager.getInstance().error("Ошибка сжатия изображения для галереи.", e);
            }
        }
    }
}
