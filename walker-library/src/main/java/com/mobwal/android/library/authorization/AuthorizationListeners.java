package com.mobwal.android.library.authorization;

import android.content.Context;

import androidx.annotation.NonNull;

public interface AuthorizationListeners {
    /**
     * Авторизация при наличии интернет соединения
     */
    int ONLINE = 0;

    /**
     * Авторизация при отсутствии интернета
     */
    int OFFLINE = 1;

    /**
     * авторизация
     *
     * @param context контекст
     * @param login логин
     * @param password пароль
     * @return результат авторизации
     */
    AuthorizationMeta authorization(@NonNull Context context, @NonNull String login, @NonNull String password);

    /**
     * Преобразование статуса ответа в мета-информацию
     *
     * @param response ответ от сервера в формате JSON
     * @param code код
     * @return мета информация
     */
    AuthorizationMeta convertResponseToMeta(@NonNull Context context, @NonNull String response, int code);
}
