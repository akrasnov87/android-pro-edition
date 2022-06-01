package com.mobwal.pro.models.db;

import android.content.Context;

import androidx.annotation.Nullable;

import java.text.MessageFormat;
import java.util.Date;
import java.util.UUID;

import com.mobwal.pro.R;
import com.mobwal.pro.annotation.TableMetaData;
import com.mobwal.pro.utilits.DateUtil;

@TableMetaData(name = "cd_routes", to = false)
public class Route {
    public Route() {
        id = UUID.randomUUID().toString();
        d_date = new Date();
        b_check = false;
    }

    public String id;

    public String c_name;

    @Nullable
    public String c_description;

    @Nullable
    public Date d_date;
    
    public boolean b_check;
}
