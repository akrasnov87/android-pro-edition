package com.mobwal.pro.models.db.complex;

import android.content.Context;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

import com.mobwal.pro.models.db.Point;
import com.mobwal.android.library.util.StringUtil;

public class PointItem implements Serializable {
    public PointItem() {
        jb_data = null;
    }

    public PointItem(Point point) {
        id = point.id;
        c_address = point.c_address;
        b_done = false;
        c_description = point.c_description;
        jb_data = point.jb_data;
        b_anomaly = false;
        b_check = true;
    }

    public String id;
    public String c_address;
    public boolean b_done;
    @Nullable
    public String c_description;
    @Nullable
    public String jb_data;
    public boolean b_anomaly;
    public boolean b_check;
    public boolean b_server;

    @Nullable
    public String toUserString(@NonNull Context context) {
        return StringUtil.convertTemplate(context, c_description, jb_data);
    }
}
