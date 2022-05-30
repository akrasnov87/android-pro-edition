package com.mobwal.pro.models.db;

import androidx.annotation.Nullable;

import com.mobwal.pro.MetaTableName;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

public class cd_attachments implements Serializable {

    public static MetaTableName Meta = new MetaTableName("dbo", "cd_attachments");

    public cd_attachments() {
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
