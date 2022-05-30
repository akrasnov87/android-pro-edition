package com.mobwal.pro.models.db;

import android.location.Location;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.mobwal.pro.MetaTableName;

import java.util.Date;
import java.util.UUID;

public class cd_results {

    public static MetaTableName Meta = new MetaTableName("dbo", "cd_results");

    public cd_results() {
        this(null);
    }

    public cd_results(String f_point) {
        id = UUID.randomUUID().toString();
        n_latitude = null;
        n_longitude = null;
        d_date = new Date();

        this.fn_point = f_point;
    }

    public cd_results(String id, String f_route, String f_point, String fn_template, @Nullable Location location, @Nullable Point point) {
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

    /**
     * Тип операции надл объектом
     */
    public String __OBJECT_OPERATION_TYPE;

    /**
     * Запись была удалена или нет
     */
    public boolean __IS_DELETE;

    /**
     * Была произведена синхронизация или нет
     */
    public boolean __IS_SYNCHRONIZATION;

    /**
     * идентификатор транзакции
     */
    public String __TID;

    /**
     * идентификатор блока
     */
    public String __BLOCK_TID;
}
