package com.mobwal.pro;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;

import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import com.mobwal.android.library.BitmapCache;
import com.mobwal.android.library.FileManager;
import com.mobwal.android.library.PrefManager;
import com.mobwal.android.library.authorization.AuthorizationRequest;
import com.mobwal.android.library.data.sync.util.transfer.Transfer;
import com.mobwal.android.library.exception.FaceExceptionSingleton;
import com.mobwal.android.library.util.ImageUtil;

import com.mobwal.android.library.authorization.BasicAuthorizationSingleton;
import com.mobwal.android.library.exception.ExceptionInterceptListeners;
import com.mobwal.android.library.exception.MyUncaughtExceptionHandler;

public class WalkerApplication extends Application implements ExceptionInterceptListeners {

    private boolean isAuthorized = false;
    private static boolean ReportSending = false;
    public static boolean Debug = false;

    private static final BitmapCache sBitmapCache = new BitmapCache();

    private WalkerSQLContext mWalkerSQLContext;

    /**
     * Сохранение данных в кэше
     * @param key ключ
     * @param bitmap изображение
     */
    public synchronized static void cacheBitmap(@NotNull String key, @NotNull Bitmap bitmap) {
        sBitmapCache.put(key, bitmap);
    }

    /**
     * Получение данных из кэш
     * @param key ключ
     * @return изображение
     */
    @Nullable
    public synchronized static Bitmap getBitmap(@NotNull String key) {
        return sBitmapCache.get(key);
    }

    /**
     * Получение изображения
     * @param key ключ
     * @param bytes массив байтов
     * @param desiredWidth предполагаемый размер
     * @return изображение
     */
    public synchronized static Bitmap getBitmap(@NonNull String key, @NonNull byte[] bytes, int desiredWidth) {
        Bitmap cache = WalkerApplication.getBitmap(key);
        if(cache == null) {
            Bitmap bitmap = ImageUtil.getSizedBitmap(bytes, 0, bytes.length, desiredWidth);
            WalkerApplication.cacheBitmap(key, bitmap);
            return bitmap;
        } else {
            return cache;
        }
    }

    /**
     * Установка призначка авторизации в приложении
     * @param context контекст
     * @param authorized признак авторизации
     */
    @Deprecated
    public static void setAuthorized(Context context, boolean authorized) {
        WalkerApplication app = (WalkerApplication)context.getApplicationContext();
        app.isAuthorized = authorized;
    }

    /**
     * Получение признака авторизации
     * @param context текущий контекст
     * @return возвращается признак авторизации
     */
    @Deprecated
    public static boolean getAuthorized(Context context) {
        WalkerApplication app = (WalkerApplication)context.getApplicationContext();
        return app.isAuthorized;
    }

    /**
     * Подключение к БД
     * @param context контекст
     * @return подключение
     */
    public static WalkerSQLContext getWalkerSQLContext(Context context) {
        WalkerApplication app = (WalkerApplication)context.getApplicationContext();
        return app.mWalkerSQLContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        Transfer.CHUNK = 8;
        Transfer.STATUS_TRANSFER_SPEED = true;

        mWalkerSQLContext = new WalkerSQLContext(this, "walker");

        SharedPreferences sharedPreferences = getSharedPreferences(Names.PREFERENCE_NAME, MODE_PRIVATE);

        Debug = sharedPreferences.getBoolean("debug", false);
        ReportSending = sharedPreferences.getBoolean("error_reporting", false);

        FirebaseCrashlytics.getInstance().setCrashlyticsCollectionEnabled(ReportSending);

        if(ReportSending) {
            FirebaseCrashlytics.getInstance().setCustomKey("debug", Debug);
            FirebaseCrashlytics.getInstance().setCustomKey("pin_use", !sharedPreferences.getString("pin_code", "").isEmpty());
        }

        BasicAuthorizationSingleton.createInstance(this, Names.CLAIMS, new AuthorizationRequest(Names.getConnectUrl()));
    }

    /**
     * Логирование действий пользователя
     * @param message сообщение
     */
    @Deprecated
    public static void Log(String message) {
        Log(message, null);
    }

    /**
     * Логирование действий пользователя
     * @param message сообщение
     * @param exception исключение
     */
    @Deprecated
    public static void Log(String message, @Nullable Exception exception) {
        if(ReportSending) {
            if(exception != null) {
                FirebaseCrashlytics.getInstance().recordException(exception);
            }
            FirebaseCrashlytics.getInstance().log(message);
        }
    }

    /**
     * Логирование действий пользователя в режиме отладки
     * @param message сообщение
     */
    @Deprecated
    public static void Debug(@NotNull String message) {
        Debug(message, null);
    }

    /**
     * Логирование действий пользователя в режиме отладки
     * @param message сообщение
     * @param exception исключение
     */
    @Deprecated
    public static void Debug(@NotNull String message, @Nullable Exception exception) {
        if(Debug && ReportSending) {
            if(exception != null) {
                FirebaseCrashlytics.getInstance().recordException(exception);
            }
            FirebaseCrashlytics.getInstance().log(message);
        }
    }

    /**
     * Авторизация выполнена
     * @param activity контекст
     */
    public static void authorized(@NonNull Activity activity) {
        activity.finish();

        FileManager.createInstance(BasicAuthorizationSingleton.getInstance().getUser().getCredential(), activity);

        activity.startActivity(MainActivity.getIntent(activity));
    }

    /**
     * Выход из приложения
     * @param context контекст
     */
    public static void exitToApp(@NotNull Context context) {
        new PrefManager(context).clearAll();
        BasicAuthorizationSingleton.getInstance().destroy();
    }

    @Override
    public void onExceptionIntercept() {
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(this, Thread.getDefaultUncaughtExceptionHandler(), getExceptionGroup(), getExceptionCode()));
    }

    @Override
    public String getExceptionGroup() {
        return "APP";
    }

    @Override
    public int getExceptionCode() {
        return 0;
    }
}
