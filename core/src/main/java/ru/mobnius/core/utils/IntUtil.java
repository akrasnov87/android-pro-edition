package ru.mobnius.core.utils;

public class IntUtil {
    /**
     * Преобразование в int
     * @param value значение
     * @return возвращается int
     */
    public static int convertToInt(Object value){
        return Integer.parseInt(String.valueOf(value));
    }

    /**
     * Преобразование значение в строку
     * @param value значение
     * @return строка
     */
    public static String toStringValue(Integer value) {
        return value == null ? "" : String.valueOf(value);
    }
}
