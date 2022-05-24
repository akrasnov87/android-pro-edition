package ru.mobnius.core.utils;

import ru.mobnius.core.data.Version;

public class AuthUtil {
    private static final int MIN_LENGTH = 3;

    /**
     * Проверка на минимальную длину
     *
     * @param field содержимое поля
     * @return строка с результатом. Если String.Empty, то все хорошо.
     */
    public static String minLength(String field) {
        if (field.length() >= MIN_LENGTH) {
            return "";
        } else {
            return "Минимальная длина должна быть " + MIN_LENGTH + " символа (ов).";
        }
    }

    /**
     * Проверка на пустое поле
     *
     * @param login    содержимое поля логин
     * @param password содержимое поля пароль
     * @return нужно ли активировать кнопку.
     */
    public static boolean isButtonEnable(String login, String password) {
        return login.length() >= MIN_LENGTH && password.length() >= MIN_LENGTH;
    }

    /**
     * Проверка шаблона сообщения
     *
     * @param template шаблон
     */
    private static boolean isValidTemplate(String template) {
        int count = -1;
        int i = -1;
        do {
            i++;
            count++;
            i = template.indexOf("%s", i);
        } while (i >= 0);

        return count == 4;
    }

    /**
     * Вывод сообщения о версии приложения
     *
     * @param template    шаблон строки
     * @param versionName Версия
     * @param status      статус приложения
     * @return строка
     */
    public static String getVersionToast(String template, String versionName, String status) {
        if (isValidTemplate(template)) {
            Version mVersion = new Version();
            int[] parts = mVersion.getVersionParts(versionName);
            String userDateString = DateUtil.convertDateToUserString(mVersion.getBuildDate(Version.BIRTH_DAY, versionName));

            return String.format(template, parts[0], parts[1], userDateString, status);
        }

        return null;
    }
}

