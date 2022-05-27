package com.mobwal.pro.models.db;

import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class Attachment implements Serializable {

    public Attachment() {
        id = UUID.randomUUID().toString();
        d_date = new Date();
        n_latitude = null;
        n_longitude = null;
        n_distance = null;
    }

    public String id;

    public String fn_route;

    public String fn_point;

    public String fn_result;

    @Nullable
    public Double n_latitude;

    @Nullable
    public Double n_longitude;

    public Date d_date;

    public String c_path;

    public int n_size;

    public String c_extension;

    public String c_mime;

    public String jb_data;

    @Nullable
    public Double n_distance;

    public String fn_storage;
}
