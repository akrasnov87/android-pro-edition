package ru.mobnius.core.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.mobnius.core.R;
import ru.mobnius.core.adapter.holder.SyncLogHolder;
import ru.mobnius.core.model.LogItemModel;

public class SyncLogAdapter extends RecyclerView.Adapter<SyncLogHolder> {
    private List<LogItemModel> mList;
    private Context mContext;

    public SyncLogAdapter(Context context, List<LogItemModel> list) {
        this.mList = list;
        this.mContext = context;
    }

    @NonNull
    @Override
    public SyncLogHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.sync_log_item, parent, false);
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
