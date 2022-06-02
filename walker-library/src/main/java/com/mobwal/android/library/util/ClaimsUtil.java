package com.mobwal.android.library.util;

import androidx.annotation.NonNull;

import com.mobwal.android.library.data.Claims;

import java.util.ArrayList;
import java.util.List;

/**
 * Служебный класс для работы с ролями
 */
public class ClaimsUtil {
    private final Claims[] mClaims;

    public ClaimsUtil(@NonNull String claims) {
        String[] data = claims.split("\\.");

        List<Claims> list = new ArrayList<>();

        for (String item : data) {
            if (!item.isEmpty()) {
                list.add(new Claims(item));
            }
        }
        mClaims = list.toArray(new Claims[0]);
    }

    /**
     * Проверка на доступность роли
     *
     * @param claimName имя роли
     * @return результат
     */
    public boolean isExists(@NonNull String claimName) {
        for (Claims item : mClaims) {
            if (item.getName().equals(claimName)) {
                return true;
            }
        }
        return false;
    }
}
