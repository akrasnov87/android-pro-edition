package com.mobwal.pro.adapter;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.mobwal.pro.R;
import com.mobwal.pro.adapter.holder.SyncLogHolder;
import com.mobwal.pro.sync.SynchronizationLogItem;

import java.util.List;

public class SyncLogAdapter extends RecyclerView.Adapter<SyncLogHolder> {
    private List<SynchronizationLogItem> mList;
    private Context mContext;

    public SyncLogAdapter(Context context, List<SynchronizationLogItem> list) {
        this.mList = list;
        this.mContext = context;
    }

    @NonNull
    @Override
    public SyncLogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.synchronization_log_item, parent, false);
        return new SyncLogHolder(mContext, view);
    }

    @Override
    public void onBindViewHolder(@NonNull SyncLogHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }
}
