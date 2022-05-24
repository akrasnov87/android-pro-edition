package ru.mobnius.core.data.configuration;


public class ConfigurationSetting {

    public final static String INTEGER = "INTEGER";
    public final static String REAL = "REAL";
    public final static String TEXT = "TEXT";
    public final static String BOOLEAN = "BOOLEAN";
    public final static String DATE = "DATE";

    public ConfigurationSetting() {}

    public ConfigurationSetting(String key, String value, String type) {
        this();

        this.key = key;
        this.value = value;
        this.type = type;
    }

    public ConfigurationSetting(String key, String value, String label, String summary, String type) {
        this(key, value, type);

        this.label = label;
        this.summary = summary;
    }

    /**
     * Ключ
     */
    public String key;
    /**
     * Значение
     */
    public String value;
    public String label;
    /**
     * Описание
     */
    public String summary;
    /**
     * Тип значения в настройке.
     * INTEGER, REAL, TEXT, BLOB
     */
    public String type;
}
