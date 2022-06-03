package com.mobwal.pro.models.db;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.mobwal.android.library.annotation.TableMetaData;
import com.mobwal.android.library.data.sync.OnAttachmentListeners;

import java.io.Serializable;
import java.util.Date;
import java.util.UUID;

@TableMetaData(name = "attachments", useMUIFunction = false)
public class Attachment implements Serializable, OnAttachmentListeners {

    public Attachment() {
        id = UUID.randomUUID().toString();
        d_date = new Date();
        n_latitude = null;
        n_longitude = null;
        n_distance = null;
    }

    @Expose
    public String id;

    @Expose
    public String fn_route;

    @Expose
    public String fn_point;

    @Expose
    public String fn_result;

    @Expose
    @Nullable
    public Double n_latitude;

    @Expose
    @Nullable
    public Double n_longitude;

    @Expose
    public Date d_date;

    @Expose
    public String c_path;

    @Expose
    @Nullable
    public Long n_distance;

    /**
     * Тип операции над объектом
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
