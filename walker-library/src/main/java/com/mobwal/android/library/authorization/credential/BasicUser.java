package com.mobwal.android.library.authorization.credential;

import androidx.annotation.NonNull;

/**
 * Объект пользователя, авторизовавшегося в приложении
 */
public class BasicUser {
    private final String mClaims;

    private final BasicCredential mCredential;
    private final Long mUserId;
    private final String[] mRoles;

    public BasicUser(@NonNull BasicCredential credentials, long userId, @NonNull String claims) {
        mCredential = credentials;
        mClaims = claims;
        mUserId = userId;

        String trimClaims = claims.replaceAll("^.", "").replaceAll(".$", "");
        mRoles = trimClaims.split("\\.");
    }

    public boolean userInRole(@NonNull String roleName) {
        for(String s : mRoles){
            if(s.equals(roleName))
                return  true;
        }

        return false;
    }

    public Long getUserId() {
        return mUserId;
    }

    /**
     * Возращается объект с данным для авторизации
     * @return данные об авторизации
     */
    public BasicCredential getCredential() {
        return mCredential;
    }

    /**
     * список ролей у пользователя. Разделителем является точка
     * @return список
     */
    public String getClaims() {
        return mClaims;
    }
}

