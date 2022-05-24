package ru.mobnius.core.srv;

import com.google.gson.annotations.Expose;

public class MobileDevice {
    @Expose
    public boolean b_debug;

    /**
     * Версия приложения
     */
    @Expose
    public String c_version;

    /**
     * Архитектура устройства
     */
    @Expose
    public String c_architecture;

    /**
     * IMEI
     */
    @Expose
    public String c_imei;

    /**
     * Версия ОС
     */
    @Expose
    public String c_os;

    /**
     * Модель телефона
     */
    @Expose
    public String c_phone_model;

    /**
     * Версия sdk
     */
    @Expose
    public String c_sdk;

    /**
     * Дата возникновения событий
     */
    @Expose
    public String d_date;

    /**
     * Пользователь
     */
    @Expose
    public long fn_user;
}
