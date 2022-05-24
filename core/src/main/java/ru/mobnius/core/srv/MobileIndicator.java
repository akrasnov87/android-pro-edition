package ru.mobnius.core.srv;

import com.google.gson.annotations.Expose;

public class MobileIndicator {
    /**
     * Состояние подключения к сети интернет
     */
    @Expose
    public boolean b_isonline;

    /**
     * Тип сети
     */
    @Expose
    public String c_network_type;

    /**
     * Дата события
     */
    @Expose
    public String d_date;

    /**
     * Пользователь
     */
    @Expose
    public long fn_user;

    /**
     * Уровень заряда батареи
     */
    @Expose
    public long n_battery_level;

    /**
     * Размер внутренней памяти
     */
    @Expose
    public long n_phone_memory;

    /**
     * Размер ОЗУ
     */
    @Expose
    public long n_ram;

    /**
     * Размер внешней памяти
     */
    @Expose
    public long n_sd_card_memory;

    /**
     * Смещение времени
     */
    @Expose
    private long n_time;

    /**
     * Размер используемой внутренней памяти
     */
    @Expose
    public long n_used_phone_memory;

    /**
     * Размер используемого ОЗУ
     */
    @Expose
    public long n_used_ram;

    /**
     * Размер используемой внешей памяти
     */
    @Expose
    public long n_used_sd_card_memory;
}
