package com.mobwal.pro.models.db;

import android.location.Location;
import android.location.LocationManager;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.mobwal.android.library.annotation.TableMetaData;

import org.osmdroid.util.GeoPoint;

import java.util.UUID;

@TableMetaData(name = "cd_points")
public class Point {
    public Point() {
        id = UUID.randomUUID().toString();
        n_latitude = null;
        n_longitude = null;
        b_anomaly = false;
        n_order = 0;
        jb_data = null;
        b_check = true;
    }

    @Expose
    public String id;

    @Expose
    public String c_address;

    @Nullable
    public Double n_latitude;

    @Nullable
    public Double n_longitude;

    @Expose
    public String c_description;

    @Expose
    public boolean b_anomaly;

    @Expose
    public String fn_route;

    @Expose
    public int n_order;

    @Expose
    @Nullable
    public String jb_data;

    public boolean b_check;

    public String c_comment;

    @Expose
    public boolean b_disabled;

    public boolean b_server;

    @Nullable
    public GeoPoint convertToLatLng() {
        if(n_latitude == null || n_longitude == null) {
            return null;
        } else {
            return new GeoPoint(n_latitude, n_longitude);
        }
    }

    @Nullable
    public Location getLocation() {
        if(n_latitude == null || n_longitude == null) {
            return null;
        } else {
            Location location = new Location(LocationManager.PASSIVE_PROVIDER);
            location.setLatitude(n_latitude);
            location.setLongitude(n_longitude);
            return location;
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
