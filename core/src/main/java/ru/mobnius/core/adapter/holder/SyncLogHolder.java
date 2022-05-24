package ru.mobnius.core.adapter.holder;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.mobnius.core.R;
import ru.mobnius.core.model.LogItemModel;
import ru.mobnius.core.utils.ColorUtil;
import ru.mobnius.core.utils.DateUtil;

public class SyncLogHolder extends RecyclerView.ViewHolder {
    private TextView tvDate;
    private TextView tvMessage;

    private Context mContext;

    public SyncLogHolder(Context context, @NonNull View itemView) {
        super(itemView);
        mContext = context;

        // чтобы при прокрутке во момент выполнения синхронизации мешанина не была.
        this.setIsRecyclable(false);

        tvDate = itemView.findViewById(R.id.sync_log_item_date);
        tvMessage = itemView.findViewById(R.id.sync_log_item_message);
    }

    public void bind(LogItemModel model) {
        tvDate.setText(DateUtil.convertDateToUserString(model.d_date, "HH:mm:ss"));
        if(model.b_error) {
            tvMessage.setTextColor(mContext.getResources().getColor(R.color.colorSecondary));
        } else {
            tvMessage.setTextColor(mContext.getResources().getColor(R.color.colorHint));
        }
        tvMessage.append(model.c_message);
    }
}
