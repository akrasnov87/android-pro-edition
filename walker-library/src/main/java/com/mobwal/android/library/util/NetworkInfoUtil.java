package com.mobwal.android.library.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkInfoUtil {
    /**
     * Проверка на доступность сети интернет
     *
     * @param context контекст
     * @return true - интернет есть
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager manager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        @SuppressLint("MissingPermission") NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        // Network is present and connected
        return networkInfo != null && networkInfo.isConnected();
    }
}