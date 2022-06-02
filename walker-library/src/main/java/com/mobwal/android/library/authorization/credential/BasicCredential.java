package com.mobwal.android.library.authorization.credential;

import android.util.Base64;

import androidx.annotation.NonNull;

/**
 * Объект для basic-authorization
 */
public class BasicCredential {
    /**
     * Логин пользователя
     */
    public final String login;

    /**
     * Пароль пользователя
     */
    public final String password;

    /**
     * Конструктор
     * @param login логин
     * @param password пароль
     */
    public BasicCredential(@NonNull String login, @NonNull String password){
        this.login = login;
        this.password = password;
    }

    /**
     * Токен авторизации, полученный в результате запроса
     * @return токен авторизации
     */
    public String getToken() {
        String str = this.login + ":" + this.password;
        byte[] bytesEncoded = Base64.encode(str.getBytes(), Base64.NO_WRAP);
        return "Token " + new String(bytesEncoded);
    }

    /**
     * Сравнение паролей
     * @param password текущей пароль
     * @return результат сравнения
     */
    public boolean isEqualsPassword(String password) {
        return this.password.equals(password);
    }

    /**
     * Метод для чтения токен
     * @param token токен авторизации
     * @return параметры безопасности
     */
    public static BasicCredential decode(@NonNull String token) {
        token = token.replace("Token ","");
        String authorization = new String(Base64.decode(token, Base64.DEFAULT));
        String[] data = authorization.split(":");
        return new BasicCredential(data[0], data[1]);
    }
}