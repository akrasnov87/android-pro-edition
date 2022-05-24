package ru.mobnius.core.utils;

public class DoubleUtil {
    /**
     * Преобразование значение в строку
     * @param value значение
     * @return строка
     */
    public static String toStringValue(Double value) {
        return value == null ? "" : String.valueOf(value);
    }
}
