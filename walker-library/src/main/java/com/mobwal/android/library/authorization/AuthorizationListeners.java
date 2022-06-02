package com.mobwal.android.library.authorization;

import android.content.Context;

import androidx.annotation.NonNull;

import com.mobwal.android.library.data.Meta;

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
     * @return мета информация
     */
    AuthorizationMeta convertResponseToMeta(@NonNull String response);

    /**
     * результат обработки обратного вызова
     * @param meta результат
     */
    void onResponseAuthorizationResult(AuthorizationMeta meta);

    /**
     * Получение имени последнего авторизованного пользователя
     * @return Логин
     */
    String getLastAuthUserName();
}
