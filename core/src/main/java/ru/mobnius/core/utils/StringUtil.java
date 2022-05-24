package ru.mobnius.core.utils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import ru.mobnius.core.Names;

public class StringUtil {
    private static final String NULL = "null";

    /**
     * Сокращение guid
     *
     * @param guid UUID
     * @return возвращается до символа -
     */
    public static String getShortGuid(String guid) {
        if (!isEmptyOrNull(guid)) {
            return guid.substring(0, guid.indexOf("-"));
        } else {
            return guid;
        }
    }

    /**
     * Корректировка строки
     *
     * @param txt входная строка
     * @return результат
     */
    public static String normalString(String txt) {
        if (txt == null) {
            return "";
        }
        return txt.equals(NULL) ? "" : txt;
    }

    /**
     * Преобразование байтов в КБ, МБ, ГБ
     *
     * @param size размер
     * @return строка
     */
    public static String getSize(long size) {
        String s;
        double kb = (double) size / 1024;
        double mb = kb / 1024;
        double gb = mb / 1024;
        double tb = gb / 1024;
        if (size < 1024) {
            s = size + " байт";
        } else if (size < 1024 * 1024) {
            s = String.format(Locale.getDefault(), "%.2f", kb) + " КБ";
        } else if (size < 1024 * 1024 * 1024) {
            s = String.format(Locale.getDefault(), "%.2f", mb) + " МБ";
        } else if (size < (long) 1024 * (long) 1024 * (long) 1024 * (long) 1024) {
            s = String.format(Locale.getDefault(), "%.2f", gb) + " ГБ";
        } else {
            s = String.format(Locale.getDefault(), "%.2f", tb) + " ТБ";
        }
        return s;
    }

    /**
     * строка является пустой или равна null
     *
     * @param input входная строка
     * @return результат сравнения
     */
    public static boolean isEmptyOrNull(String input) {
        String normal = normalString(input);
        return normal.isEmpty();
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
     * Получение mime по имени файла
     *
     * @param name имя файла
     * @return MIME-тип
     */
    public static String getMimeByName(String name) {
        String extension = getExtension(name);
        switch (extension) {
            case ".jpg":
                return "image/jpeg";

            case ".png":
                return "image/png";

            case ".webp":
                return "image/webp";

            case ".mp3":
                return "audio/mpeg";

            case Names.VIDEO_EXTENSION:
                return "video/mp4";

            default:
                return "application/octet-stream";
        }
    }

    /**
     * Получение расширения файла
     *
     * @param name имя файла
     * @return расширение
     */
    public static String getExtension(String name) {
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
        return null;
    }

    /**
     * Преобразование исключения в строку
     *
     * @param e исключение
     * @return строка
     */
    public static String exceptionToString(Throwable e) {
        Writer writer = new StringWriter();
        e.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

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
     * Null-safe обработка строки на пустоту
     *
     * @param str строка для проверки
     * @return пустую строку если строка равна null или изначальную строку
     */
    @NonNull
    public static String defaultEmptyString(final @Nullable CharSequence str) {
        return str == null ? "" : str.toString();
    }

}
