package ru.mobnius.core.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import ru.mobnius.core.data.Version;

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

    /**
     * Проверка на обновление версии
     * @param context контекст
     * @param newVersion новая версия на сервере
     * @return обновлять версию или нет
     */
    public static boolean isUpgradeVersion(Context context, String newVersion, boolean isDebug) {
        Version mVersion = new Version();
        String currentVersion = VersionUtil.getVersionName(context);
        Date currentDate = mVersion.getBuildDate(Version.BIRTH_DAY, currentVersion);
        Date serverDate = mVersion.getBuildDate(Version.BIRTH_DAY, newVersion);

        return serverDate.getTime() > currentDate.getTime()
                && (mVersion.getVersionState(newVersion) == Version.PRODUCTION || isDebug);
    }

    /**
     * установлено ли приложение из Play Store
     * @param context контекст
     * @return true - установлено из play store
     */
    public static boolean verifyInstallerId(Context context) {
        // A list with valid installers package name
        List<String> validInstallers = new ArrayList<>(Arrays.asList("com.android.vending", "com.google.android.feedback"));

        // The package name of the app that has installed your app
        final String installer = context.getPackageManager().getInstallerPackageName(context.getPackageName());

        // true if your app has been downloaded from Play Store
        return installer != null && validInstallers.contains(installer);
    }
}