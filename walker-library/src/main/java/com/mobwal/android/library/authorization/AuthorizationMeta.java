package com.mobwal.android.library.authorization;

import com.mobwal.android.library.data.Meta;

/**
 * Класс расширения мета информации для авторизации
 */
public class AuthorizationMeta extends Meta {
    /**
     * токен авторизации
     */
    private final String mToken;
    /**
     * список ролей
     */
    private final String mClaims;
    /**
     * идентификатор пользователя
     */
    private final Long mUserId;

    private final String mLogin;

    public AuthorizationMeta(int status, String message, String token, String claims, Long userId, String login) {
        super(status, message);

        mToken = token;
        mClaims = claims;
        mUserId = userId;
        mLogin = login;
    }

    public AuthorizationMeta(int status, String message) {
        this(status, message, null, null, null, null);
    }

    public String getToken() {
        return mToken;
    }

    public String getClaims() {
        return mClaims;
    }

    public Long getUserId() {
        return mUserId;
    }

    public String getLogin() {
        return mLogin;
    }
}
