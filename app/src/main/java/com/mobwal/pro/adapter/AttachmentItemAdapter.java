package com.mobwal.pro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import com.mobwal.android.library.data.DbOperationType;
import com.mobwal.pro.R;
import com.mobwal.pro.adapter.holder.AttachmentItemHolder;
import com.mobwal.pro.models.db.Attachment;
import com.mobwal.pro.ui.RecycleViewItemListeners;

public class AttachmentItemAdapter extends RecyclerView.Adapter<AttachmentItemHolder> {
    private final Context mContext;
    private final List<Attachment> mAttachments;
    private final RecycleViewItemListeners mListeners;

    public AttachmentItemAdapter(@NotNull Context context, @NonNull List<Attachment> attachments, @Nullable RecycleViewItemListeners listeners) {
        mContext = context;
        mAttachments = attachments;
        mListeners = listeners;
    }

    @NonNull
    @Override
    public AttachmentItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.attachment_item, parent, false);
        return new AttachmentItemHolder(view, mListeners);
    }

    @Override
    public void onBindViewHolder(@NonNull AttachmentItemHolder holder, int position) {
        holder.bind(mAttachments.get(position));
    }

    @Override
    public int getItemCount() {
        return mAttachments.size();
    }

    /**
     * Добавление вложения
     * @param attachment объект вложения
     */
    public void add(@NotNull Attachment attachment) {
        mAttachments.add(attachment);
        notifyItemInserted(mAttachments.size() - 1);
    }

    /**
     * Возвращается массив данных
     * @return массив данных
     */
    public List<Attachment> getData() {
        return mAttachments;
    }

    /**
     * Удаление вложения
     * @param id иден. вложения
     */
    public void removeAttachment(String id) {
        int idx = 0;
        Attachment removeAttach = null;
        for (Attachment attach: mAttachments) {
            if (attach.id.equals(id)) {
                removeAttach = attach;
                break;
            }
            idx++;
        }

        if(removeAttach != null) {
            if(removeAttach.b_server) {
                removeAttach.b_disabled = true;
                removeAttach.__IS_SYNCHRONIZATION = false;
                removeAttach.__OBJECT_OPERATION_TYPE = DbOperationType.UPDATED;

                notifyItemChanged(idx);
            } else {
                mAttachments.remove(removeAttach);
                notifyItemRemoved(idx);
            }
        }
    }
}
