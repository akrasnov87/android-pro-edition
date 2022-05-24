package ru.mobnius.core.utils;

public class UrlUtil {
    /**
     * получение из адресной строки имени домена
     *
     * @param url адресная строка
     * @return доменное имя с протоколом
     */
    public static String getDomainUrl(String url) {
        int count = 0;
        StringBuilder builder = new StringBuilder(url.length());

        for (int i = 0; i < url.length(); i++) {
            char ch = url.charAt(i);
            if (ch == '/') {
                count++;
            }

            if (count >= 3) { // идет подсчет количества слешей
                break;
            }
            builder.append(ch);
        }
        return builder.toString();
    }

    /**
     * Получение виртуального пути из адресной строки
     *
     * @param url адресная строка
     * @return виртуальный путь
     */
    public static String getPathUrl(String url) {
        String domain = getDomainUrl(url);
        return url.replace(domain, "");
    }
}
