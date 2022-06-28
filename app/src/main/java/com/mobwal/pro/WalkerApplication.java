package com.mobwal.pro;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.osmdroid.config.Configuration;

import com.mobwal.android.library.BitmapCache;
import com.mobwal.android.library.Constants;
import com.mobwal.android.library.PrefManager;
import com.mobwal.android.library.authorization.AuthorizationRequest;
import com.mobwal.android.library.authorization.credential.BasicUser;
import com.mobwal.android.library.data.DbOperationType;
import com.mobwal.android.library.data.sync.util.transfer.Transfer;
import com.mobwal.android.library.exception.MaterialException;
import com.mobwal.android.library.exception.ExceptionHandler;
import com.mobwal.android.library.util.ImageUtil;

import com.mobwal.android.library.authorization.BasicAuthorizationSingleton;
import com.mobwal.android.library.exception.ExceptionInterceptListeners;
import com.mobwal.android.library.exception.MyUncaughtExceptionHandler;
import com.mobwal.android.library.LogManager;
import com.mobwal.android.library.util.VersionUtil;
import com.mobwal.pro.models.db.Audit;
import com.mobwal.pro.models.db.MobileDevice;

import java.util.List;
import java.util.UUID;

public class WalkerApplication extends Application implements ExceptionInterceptListeners {
    public static String sSessionId = UUID.randomUUID().toString();

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
     * Подключение к БД
     * @param context контекст
     * @return подключение
     */
    public static WalkerSQLContext getWalkerSQLContext(Context context) {
        WalkerApplication app = (WalkerApplication)context.getApplicationContext();
        return app.mWalkerSQLContext;
    }

    /**
     * Установка подключения к БД
     * @param context контекст
     * @param sqlContext подключение
     */
    public static void setWalkerSQLContext(Context context, WalkerSQLContext sqlContext) {
        WalkerApplication app = (WalkerApplication)context.getApplicationContext();
        app.mWalkerSQLContext = sqlContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        LogManager.createInstance(new LogInMemoryWriter(sSessionId));

        // без этого сайт osm не возвращает результат
        Configuration.getInstance().setUserAgentValue(BuildConfig.APPLICATION_ID);

        // TODO: после тестирования убрать
        //Transfer.CHUNK = 10 * 1024 * 1024;
        //Transfer.STATUS_TRANSFER_SPEED = true;

        PrefManager prefManager = new PrefManager(this);
        if(prefManager.get("error_reporting", false)) {
            LogManager.getInstance().debug("debug=" + prefManager.get("debug", false));
        }

        BasicAuthorizationSingleton.createInstance(this, Names.CLAIMS, new AuthorizationRequest(Names.getConnectUrl()));
    }

    /**
     * Авторизация выполнена
     * @param activity контекст
     */
    public static void authorized(@NonNull Activity activity) {
        BasicUser basicUser = BasicAuthorizationSingleton.getInstance().getUser();
        WalkerSQLContext walkerSQLContext = new WalkerSQLContext(activity, basicUser.getUserId().toString());
        setWalkerSQLContext(activity, walkerSQLContext);

        // меняем способ логирования на БД
        LogInDbWriter logInDb = new LogInDbWriter(walkerSQLContext, sSessionId);
        if(LogManager.getInstance() instanceof LogInMemoryWriter) {
            LogInMemoryWriter logInMemory = (LogInMemoryWriter) LogManager.getInstance();
            logInDb.writeArray(logInMemory.getAudits().toArray(new Audit[0]));
            LogManager.createInstance(logInDb);
        }
        ExceptionHandler exceptionHandler = new ExceptionHandler(activity);

        if(exceptionHandler.getCount() > 0) {
            boolean isDebug = new PrefManager(activity).get(Constants.DEBUG, false);
            List<MaterialException> list = exceptionHandler.getExceptionList();
            if(list != null) {
                for (MaterialException faceException : list) {
                    faceException.label = faceException.getExceptionCode(isDebug);
                    LogManager.getInstance().error(faceException.toString());
                }
            }

            exceptionHandler.clearAll();
        }

        // записываем информацию об устройстве в лог
        MobileDevice mobileDevice = new MobileDevice();
        mobileDevice.b_debug = new PrefManager(activity).get("debug", false);
        mobileDevice.fn_user = basicUser.getUserId();
        mobileDevice.c_architecture = System.getProperty("os.arch");
        mobileDevice.c_model = Build.MODEL;
        mobileDevice.c_sdk = String.valueOf(Build.VERSION.SDK_INT);
        mobileDevice.c_os = Build.VERSION.RELEASE;
        mobileDevice.c_version = VersionUtil.getVersionName(activity);
        mobileDevice.c_session_id = sSessionId;
        mobileDevice.c_ip = basicUser.getIp();
        mobileDevice.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;

        walkerSQLContext.insert(mobileDevice);

        activity.finish();
        activity.startActivity(MainActivity.getIntent(activity));
    }

    /**
     * Выход из приложения
     * @param context контекст
     */
    public static void exitToApp(@NotNull Context context) {
        sSessionId = UUID.randomUUID().toString();

        new PrefManager(context).clearAll();
        BasicAuthorizationSingleton.getInstance().destroy();
        getWalkerSQLContext(context).close();
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
