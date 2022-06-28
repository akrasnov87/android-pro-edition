package com.mobwal.pro.models.db.complex;

import android.content.Context;

import java.text.MessageFormat;
import java.util.Date;

import com.mobwal.pro.R;

public class RouteItem {
    public String id;
    public String c_number;
    public int n_task;
    public int n_done;
    /**
     * задание имеет статус "Не подтверждено"
     */
    public int n_fail;
    public Date d_date;

    public String toUserString(Context context) {
        return MessageFormat.format(context.getString(R.string.route_item_subtitle) + ": {0}" + (n_fail > 0 ? "/{2}" : "") + " из {1}.", n_task - n_done, n_task, n_fail);
    }
}
