package com.mobwal.pro.models.db;

import com.google.gson.annotations.Expose;
import com.mobwal.android.library.annotation.TableMetaData;

import java.util.Date;
import java.util.UUID;

@TableMetaData(name = "ad_mobile_devices", from = false)
public class MobileDevice {

    public MobileDevice() {
        id = UUID.randomUUID().toString();
    }

    public String id;

    @Expose
    public long fn_user;

    @Expose
    public Date d_date = new Date();

    @Expose
    public boolean b_debug;

    @Expose
    public String c_architecture;

    @Expose
    public String c_model;

    @Expose
    public String c_sdk;

    @Expose
    public String c_os;

    @Expose
    public String c_version;

    @Expose
    public String c_session_id;

    @Expose
    public String c_ip;

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
