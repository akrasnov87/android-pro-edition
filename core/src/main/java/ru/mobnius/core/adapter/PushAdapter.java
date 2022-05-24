package ru.mobnius.core.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import ru.mobnius.core.R;
import ru.mobnius.core.adapter.holder.PushItemHolder;
import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.model.PushItemModel;

public class PushAdapter extends RecyclerView.Adapter<PushItemHolder> {
    private List<PushItemModel> mList;
    private Context mContext;

    public PushAdapter(Context context) {
        mList = new ArrayList<>();
        this.mContext = context;

        if(mContext instanceof PushAsyncTask.OnPushListener) {
            new PushAsyncTask(GlobalSettings.getConnectUrl(), mContext).execute();
        }
    }

    @NonNull
    @Override
    public PushItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.push_list_item, parent, false);
        return new PushItemHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PushItemHolder holder, int position) {
        holder.bind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void updateList(List<PushItemModel> items) {
        mList.clear();
        mList.addAll(items);
        notifyDataSetChanged();
    }
}
