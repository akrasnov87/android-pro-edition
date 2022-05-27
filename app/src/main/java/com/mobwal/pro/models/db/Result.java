package com.mobwal.pro.models.db;

import android.location.Location;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.UUID;

public class Result {
    public Result() {
        this(null);
    }

    public Result(String f_point) {
        id = UUID.randomUUID().toString();
        n_latitude = null;
        n_longitude = null;
        d_date = new Date();

        this.fn_point = f_point;
    }

    public Result(String id, String f_route, String f_point, String fn_template, @Nullable Location location, @Nullable Point point) {
        this.id = id;

        this.fn_route = f_route;
        this.fn_point = f_point;
        this.fn_template = fn_template;
        setLocation(location);
        setDistance(point, location);

        d_date = new Date();
    }

    public String id;

    public String fn_route;

    public String fn_point;

    @Nullable
    public Double n_latitude;

    @Nullable
    public Double n_longitude;

    @Nullable
    public String jb_data;

    public Date d_date;

    public String fn_template;

    @Nullable
    public Double n_distance;

    @Nullable
    public LatLng convertToLatLng() {
        if(n_latitude == null || n_longitude == null) {
            return null;
        } else {
            return new LatLng(n_latitude, n_longitude);
        }
    }

    public void setLocation(@Nullable Location location) {
        if (location != null) {
            n_longitude = location.getLongitude();
            n_latitude = location.getLatitude();
        }
    }

    public void setDistance(@Nullable Point point, @Nullable Location position) {
        if(point != null && position != null && point.getLocation() != null) {
            n_distance = (double) point.getLocation().distanceTo(position);
        }
    }
}
