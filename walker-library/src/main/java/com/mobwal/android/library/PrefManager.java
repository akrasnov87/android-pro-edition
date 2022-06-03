package com.mobwal.android.library;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

/**
 * Настройки в SharedPreferences
 */
public class PrefManager {
    private final SharedPreferences mSharedPreferences;

    /**
     *
     * @param context контекст
     */
    public PrefManager(@NonNull Context context) {
        this(context, "walker");
    }

    /**
     *
     * @param context контекст
     * @param preferenceName имя файла настроек
     */
    public PrefManager(@NonNull Context context, @NonNull String preferenceName) {
        mSharedPreferences = context.getSharedPreferences(preferenceName, Context.MODE_PRIVATE);
    }

    /**
     * Записать информацию
     * @param name ключ
     * @param value значение
     */
    public void put(@NonNull String name, int value) {
        mSharedPreferences.edit().putInt(name, value).apply();
    }

    /**
     * Записать информацию
     * @param name ключ
     * @param value значение
     */
    public void put(@NonNull String name, String value) {
        mSharedPreferences.edit().putString(name, value).apply();
    }

    /**
     * Записать информацию
     * @param name ключ
     * @param value значение
     */
    public void put(@NonNull String name, boolean value) {
        mSharedPreferences.edit().putBoolean(name, value).apply();
    }

    /**
     * Получение информации
     * @param name ключ
     * @param defaultValue значение по умолчанию
     * @return результат получения данных
     */
    public int get(@NonNull String name, int defaultValue) {
        return mSharedPreferences.getInt(name, defaultValue);
    }

    /**
     * Получение информации
     * @param name ключ
     * @param defaultValue значение по умолчанию
     * @return результат получения данных
     */
    public String get(@NonNull String name, String defaultValue) {
        return mSharedPreferences.getString(name, defaultValue);
    }

    /**
     * Получение информации
     * @param name ключ
     * @param defaultValue значение по умолчанию
     * @return результат получения данных
     */
    public boolean get(@NonNull String name, boolean defaultValue) {
        return mSharedPreferences.getBoolean(name, defaultValue);
    }

    /**
     * Очистка настроек
     */
    public void clearAll() {
        mSharedPreferences.edit().clear().apply();
    }
}
