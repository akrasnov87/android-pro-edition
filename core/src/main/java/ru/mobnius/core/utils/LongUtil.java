package ru.mobnius.core.utils;

public class LongUtil {
    /**
     * Преобразование в long
     * @param value значение
     * @return возвращается long
     */
    public static long convertToLong(Object value){
        return Long.parseLong(String.valueOf(value));
    }

    /**
     * Преобразование значение в строку
     * @param value значение
     * @return строка
     */
    public static String toStringValue(Long value) {
        return value == null ? "" : String.valueOf(value);
    }
}
