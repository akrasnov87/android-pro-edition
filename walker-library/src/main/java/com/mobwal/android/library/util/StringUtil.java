package com.mobwal.android.library.util;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mobwal.android.library.R;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;
import java.util.Set;

public class StringUtil {
    private static final String NULL = "null";

    /**
     * Корректировка строки
     * @param txt входная строка
     * @return результат
     */
    public static String normalString(String txt) {
        if(txt == null) {
            return "";
        }
        return txt.equals(NULL) ? "" : txt;
    }

    /**
     * строка является пустой или равна null
     * @param input входная строка
     * @return результат сравнения
     */
    public static boolean isEmptyOrNull(String input) {
        String normal = normalString(input);
        return normal.isEmpty();
    }

    /**
     * Преобразование байтов в КБ, МБ, ГБ
     * @param size размер
     * @return строка
     */
    public static String getSize(@Nullable Context context, long size) {
        String s;
        double kb = (double) size / 1024;
        double mb = kb / 1024;
        double gb = mb / 1024;
        double tb = gb / 1024;
        if(size < 1024) {
            s = size + " " + (context == null ? "байт" : context.getString(R.string.byt));
        } else if(size < 1024 * 1024) {
            s =  String.format(Locale.getDefault(), "%.2f", kb) + " " + (context == null ? "КБ" : context.getString(R.string.kb));
        } else if(size < 1024 * 1024 * 1024) {
            s = String.format(Locale.getDefault(),"%.2f", mb) + " " + (context == null ? "МБ" : context.getString(R.string.mb));
        } else if(size < (long) 1024 * (long) 1024 * (long) 1024 * (long) 1024) {
            s = String.format(Locale.getDefault(),"%.2f", gb) + " " + (context == null ? "ГБ" : context.getString(R.string.gb));
        } else {
            s = String.format(Locale.getDefault(),"%.2f", tb) + " " + (context == null ? "ТБ" : context.getString(R.string.tb));
        }
        return s;
    }

    /**
     * Получение расширения файла
     *
     * @param name имя файла
     * @return расширение
     */
    @Nullable
    public static String getFileExtension(String name) {
        if (name != null && !name.isEmpty()) {
            int strLength = name.lastIndexOf(".");
            if (strLength >= 0) {
                String ext = name.substring(strLength + 1).toLowerCase();
                if (ext.isEmpty()) {
                    return null;
                } else {
                    return "." + ext;
                }
            }
        }

        return "";
    }

    /**
     * Очистка имени от расширения
     * @param name имя файла
     * @return результат
     */
    public static String getNameWithOutExtension(@NonNull String name) {
        if(TextUtils.isEmpty(name)) {
            return "";
        }

        String ext = getFileExtension(name);
        if(ext != null) {
            return name.replace(ext, "");
        }

        return "";
    }

    /**
     * Преобразование исключения в строку
     *
     * @param e исключение
     * @return строка
     */
    public static String exceptionToString(@NonNull Throwable e) {
        Writer writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    /**
     * преобразование шаблона
     * @param context контекст
     * @param content исходный текст
     * @param json данные для выборки
     * @return результат форматирования
     */
    @Nullable
    public static String convertTemplate(@NonNull Context context, @Nullable String content, @Nullable String json) {
        if(!TextUtils.isEmpty(json) && !TextUtils.isEmpty(content)) {
            String newContent = content;
            try {
                JsonElement jsonElement = JsonParser.parseString(Objects.requireNonNull(json));
                JsonObject jsonObject = jsonElement.getAsJsonObject();
                Set<String> keySet = jsonObject.keySet();
                for (String key : keySet) {
                    String value;

                    if(jsonObject.get(key).isJsonNull()) {
                        continue;
                    }

                    if(jsonObject.getAsJsonPrimitive(key).isBoolean()) {
                        value = jsonObject.get(key).getAsBoolean() ? context.getString(R.string.yes) : context.getString(R.string.no);
                    } else if(jsonObject.getAsJsonPrimitive(key).isNumber()) {
                        value = jsonObject.get(key).toString();
                    } else {
                        value = jsonObject.get(key).getAsString();
                    }

                    newContent = Objects.requireNonNull(newContent).replace(key, value);
                }
                return newContent;
            } catch (Exception ignored) {
                return content;
            }
        }

        return content;
    }

    /**
     * Очистка начального и конечного символа
     * @param data строка для обработки
     * @param symbol символ для очистки
     * @return форматированная строка
     */
    public static String trimSymbol(@NonNull String data, char symbol) {
        int len = data.length();
        int st = 0;

        while ((st < len) && (data.charAt(st) <= symbol)) {
            st++;
        }
        while ((st < len) && (data.charAt(len - 1) <= symbol)) {
            len--;
        }
        return ((st > 0) || (len < data.length())) ? data.substring(st, len) : data;
    }

    /**
     * Создание рандомной строки определенной длины sizeOfRandomString
     * @param sizeOfRandomString длина рандомной строки
     * @return строка
     */
    public static String getRandomString(final int sizeOfRandomString) {
        String ALLOWED_CHARACTERS ="0123456789qwertyuiopasdfghjklzxcvbnm";
        final Random random = new Random();
        final StringBuilder sb = new StringBuilder(sizeOfRandomString);
        for(int i = 0;i < sizeOfRandomString; ++i) {
            sb.append(ALLOWED_CHARACTERS.charAt(random.nextInt(ALLOWED_CHARACTERS.length())));
        }
        return sb.toString();
    }

    /**
     * Получение md5-хеш кода
     *
     * @param inputString входная строка
     * @return хеш-код
     */
    public static String md5(String inputString) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest
                    .getInstance(MD5);
            digest.update(inputString.getBytes());
            byte[] messageDigest = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                StringBuilder h = new StringBuilder(Integer.toHexString(0xFF & aMessageDigest));
                while (h.length() < 2)
                    h.insert(0, "0");
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    /**
     * Заполение разделителями
     *
     * @param count     количество
     * @param separator разделитель
     * @return возвращается строка
     */
    public static String fullSpace(int count, String separator) {
        if (count > 0 && !isEmptyOrNull(separator)) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < count; i++) {
                builder.append(separator);
            }
            return builder.toString();
        } else {
            return "";
        }
    }

    /**
     * Сравнение строк без учета регистра
     * @param str1 строка 1
     * @param str2 строка 2
     * @return результат сравнения
     */
    public static boolean equalsIgnoreCase(final CharSequence str1, final CharSequence str2) {
        if (str1 == null || str2 == null) {
            return str1 == str2;
        } else if (str1 == str2) {
            return true;
        } else if (str1.length() != str2.length()) {
            return false;
        } else {
            return regionMatches(str1, str2, str1.length());
        }
    }

    private static boolean regionMatches(final CharSequence cs,
                                         final CharSequence substring, final int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(true, 0, (String) substring, 0, length);
        }
        int index1 = 0;
        int index2 = 0;
        int tmpLen = length;

        while (tmpLen-- > 0) {
            final char c1 = cs.charAt(index1++);
            final char c2 = substring.charAt(index2++);

            if (c1 == c2) {
                continue;
            }

            if (Character.toUpperCase(c1) != Character.toUpperCase(c2)
                    && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Сокращение guid
     *
     * @param guid UUID
     * @return возвращается до символа -
     */
    public static String getShortGuid(String guid) {
        if (!isEmptyOrNull(guid) && guid.indexOf("-") > 0) {
            return guid.substring(0, guid.indexOf("-"));
        } else {
            return guid;
        }
    }
}
