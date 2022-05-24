package ru.mobnius.core.data.authorization;

import ru.mobnius.core.data.Meta;

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
    private final Integer mUserId;
    private final String mUserName;

    public AuthorizationMeta(int status, String message, String token, String claims, Integer userId, String userName) {
        super(status, message);

        mToken = token;
        mClaims = claims;
        mUserId = userId;
        mUserName = userName;
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

    public String getUserName() { return mUserName; }

    public Integer getUserId() {
        return mUserId;
    }
}
