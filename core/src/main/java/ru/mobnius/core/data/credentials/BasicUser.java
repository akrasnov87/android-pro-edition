package ru.mobnius.core.data.credentials;

import ru.mobnius.core.utils.LongUtil;

/**
 * Объект пользователя, авторизовавшегося в приложении
 */
public class BasicUser {

    /**
     * список ролей у пользователя. Разделителем является точка
     */
    public final String claims;

    private final BasicCredentials credentials;
    private final Object UserId;
    private final String[] roles;

    public BasicUser(BasicCredentials credentials, Object userId, String claims){
        this.credentials = credentials;
        UserId = userId;
        String trimClaims = claims.replaceAll("^.", "").replaceAll(".$", "");
        roles = trimClaims.split("\\.");
        this.claims = claims;
    }

    public boolean userInRole(String roleName) {
        for(String s : roles){
            if(s.equals(roleName))
                return  true;
        }

        return false;
    }

    public Long getUserId() {
        return LongUtil.convertToLong(UserId);
    }

    /**
     * Возращается объект с данным для авторизации
     * @return данные об авторизации
     */
    public BasicCredentials getCredentials() {
        return credentials;
    }
}

