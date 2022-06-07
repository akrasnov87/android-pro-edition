package com.mobwal.android.library.util;

import android.util.Log;

import androidx.annotation.Nullable;

import com.mobwal.android.library.Constants;
import com.mobwal.android.library.Version;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static String SYSTEM_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    /**
     * Дата преобразуется в строку с системным форматом
     * @param date дата
     * @return возврщается строка
     */
    public static String convertDateToSystemString(Date date) {
        return new SimpleDateFormat(SYSTEM_FORMAT, Locale.getDefault()).format(date);
    }

    /**
     * Преобразование строки в дату
     * @param date дата
     * @return результат преобразования
     */
    @Nullable
    public static Date convertStringToSystemDate(String date) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat(SYSTEM_FORMAT, Locale.getDefault());
            return dateFormat.parse(date);
        } catch (Exception e) {
            Log.d(Constants.TAG, "Ошибка преобразования строки " + date + " в дату." + e);
            return null;
        }
    }

    public static String toDateTimeString(@Nullable Date date) {
        if(date != null) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.MEDIUM, DateFormat.MEDIUM, Locale.getDefault());
            return formatter.format(date);
        }

        return "";
    }

    /**
     * Дата преобразуется в строку с определнным форматом
     * @param date дата
     * @param format формат даты
     * @return возврщается строка
     */
    public static String convertDateToUserString(Date date, String format) {
        if(date == null) {
            return "";
        }
        return new SimpleDateFormat(format, Locale.getDefault()).format(date);
    }

    /**
     * Генерация TID
     * @return уникальный идентификатор
     */
    public static int generateTid() {
        return Math.abs((int)((new Date().getTime() - Version.BIRTH_DAY.getTime())));
    }
}
