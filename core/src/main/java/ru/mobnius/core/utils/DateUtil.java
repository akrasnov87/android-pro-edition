package ru.mobnius.core.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ru.mobnius.core.data.Version;

public class DateUtil {
    public static final String USER_FORMAT = "dd.MM.yyyy HH:mm:ss";
    public static final String USER_SHORT_FORMAT = "dd.MM.yyyy";
    public static final String SYSTEM_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS";

    /**
     * Преобразовать дату в пользовательскую строку
     *
     * @param date дата
     * @return возврщается строка
     */
    public static String convertDateToUserString(Date date) {
        return convertDateToUserString(date, USER_FORMAT);
    }

    /**
     * Преобразование строки в дату
     * @param time время в милисекундах
     * @return результат преобразования
     */
    public static Date convertTimeToDate(String time) {
        return new Date(Long.parseLong(time));
    }

    /**
     * Преобразование строки в дату
     * @param date дата
     * @return результат преобразования
     * @throws ParseException исключение при неверном формате
     */
    public static Date convertStringToDate(String date) throws ParseException {
        if(StringUtil.isEmptyOrNull(date)) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(SYSTEM_FORMAT, Locale.getDefault());
        return dateFormat.parse(date);
    }

    /**
     * Дата преобразуется в строку с определнным форматом
     * @param date дата
     * @return возврщается строка
     */
    public static String convertDateToString(Date date) {
        return new SimpleDateFormat(SYSTEM_FORMAT, Locale.getDefault()).format(date);
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
    public static int geenerateTid() {
        return Math.abs((int)((new Date().getTime() - Version.BIRTH_DAY.getTime())));
    }

    /**
     * Преобразование даты из системного формата в пользовательский
     * @param date строка с датой в системном формате
     * @param format формта преобразования
     * @return результат преобразования
     */
    public static String systemDateFormatToString(String date, String format) {
        try {
            Date dt = DateUtil.convertStringToDate(date);
            return convertDateToUserString(dt, format);
        } catch (ParseException e) {
            return "";
        }
    }

    /**
     * Преобразование даты из системного формата в пользовательский
     * @param date строка с датой в системном формате
     * @return результат преобразования
     */
    public static String systemDateFormatToString(String date) {
        return systemDateFormatToString(date, USER_SHORT_FORMAT);
    }

    /**
     * Преобразование даты в пользовательском формате в системную
     * @param userDate дата в пользовательском формате
     * @param userFormat формат даты
     * @return Дата в системном формате
     */
    public static String userDateFormatToSystemDateFormat(String userDate, String userFormat) {
        if(StringUtil.isEmptyOrNull(userDate)) {
            return null;
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat(userFormat, Locale.getDefault());
        try {
            Date dt = dateFormat.parse(userDate);
            return convertDateToUserString(dt, SYSTEM_FORMAT);
        } catch (ParseException e) {
            return "";
        }
    }

    public static String getMonthName(Date date, boolean isFull) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        switch (cal.get(Calendar.MONTH)) {
            case 0:
                return isFull ? "январь" : "ян";
            case 1:
                return isFull ? "февраль" : "фв";
            case 2:
                return isFull ? "март" : "мр";
            case 3:
                return isFull ? "апрель" : "ап";
            case 4:
                return isFull ? "май" : "ма";
            case 5:
                return isFull ? "июнь" : "ин";
            case 6:
                return isFull ? "июль" : "ил";
            case 7:
                return isFull ? "август" : "ав";
            case 8:
                return isFull ? "сентябрь" : "сн";
            case 9:
                return isFull ? "октябрь" : "ок";
            case 10:
                return isFull ? "ноябрь" : "нб";
            case 11:
                return isFull ? "декабрь" : "дк";

            default:
                return "nn";
        }
    }
}
