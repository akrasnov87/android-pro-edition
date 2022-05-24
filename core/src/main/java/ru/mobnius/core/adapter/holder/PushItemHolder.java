package ru.mobnius.core.adapter.holder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import ru.mobnius.core.R;
import ru.mobnius.core.model.PushItemModel;
import ru.mobnius.core.utils.ContentUtil;
import ru.mobnius.core.utils.DateUtil;

public class PushItemHolder extends RecyclerView.ViewHolder {

    private TextView tvTitle;
    private TextView tvMessage;
    private TextView tvDate;

    public PushItemHolder(@NonNull View itemView) {
        super(itemView);
        tvTitle = itemView.findViewById(R.id.push_list_item_title);
        tvMessage = itemView.findViewById(R.id.push_list_item_message);
        tvDate = itemView.findViewById(R.id.push_list_item_date);
    }

    public void bind(PushItemModel item) {
        tvTitle.setText(item.c_title);
        tvMessage.setText(item.c_message);
        tvDate.setText(DateUtil.convertDateToUserString(item.d_date));
    }
}
