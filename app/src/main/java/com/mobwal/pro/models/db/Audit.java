package com.mobwal.pro.models.db;

import com.google.gson.annotations.Expose;
import com.mobwal.android.library.annotation.TableMetaData;

import java.util.Date;
import java.util.UUID;

@TableMetaData(name = "ad_audits", from = false)
public class Audit {
    public Audit() {
        id = UUID.randomUUID().toString();
    }

    public String id;

    @Expose
    public String c_session_id;

    @Expose
    public Date d_date = new Date();

    @Expose
    public String c_data;

    @Expose
    public String c_type = "info";

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
