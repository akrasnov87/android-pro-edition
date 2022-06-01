package com.mobwal.android.library.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * Вспомогательная утилита для работы версией
 */
public class VersionUtil {
    /**
     * Возврщается версия приложения для пользователя (versionName)
     *
     * @param context activity
     * @return возвращается версия
     */
    public static String getVersionName(Context context) {
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return pInfo.versionName == null ? "0.0.0.0" : pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            return "0.0.0.0";
        }
    }

    /**
     * Возврщается укороченая версия приложения для пользователя (versionName)
     *
     * @param context activity
     * @return Возвращаются только первые два числа
     */
    public static String getShortVersionName(Context context) {
        String version = getVersionName(context);
        String[] data = version.split("\\.");
        return data[0] + "." + data[1];
    }
}
