package ru.mobnius.core.srv;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.app.OnCoreApplicationListeners;
import com.mobwal.android.library.authorization.Authorization;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.credentials.BasicUser;
import ru.mobnius.core.data.exception.IExceptionIntercept;
import ru.mobnius.core.data.exception.MyUncaughtExceptionHandler;
import ru.mobnius.core.data.exception.IExceptionGroup;
import ru.mobnius.core.data.mail.StringMail;
import ru.mobnius.core.data.socket.OnSocketListeners;
import ru.mobnius.core.utils.LocationUtil;
import ru.mobnius.core.utils.NotificationUtil;

/**
 * базовый сервис
 */
public abstract class BaseService extends Service
        implements IExceptionIntercept, OnSocketListeners, NotificationAsyncTask.OnNotificationCountListeners {

    /**
     * выполнять сбор информации по SD карте или нет
     */
    public static boolean SD_CARD_MEMORY_USAGE = true;

    protected BaseLocationListener mLocationListener;
    protected BaseTelemetryListener mTelemetryListener;
    protected BaseDeviceInfoListener mDeviceInfoListener;
    private LocationManager mLocationManager;
    private Timer mTelemetryTimer;

    private NotificationAsyncTask mNotificationAsyncTask;

    /**
     * таймер для отправки служебных данных
     */
    private final Timer mServiceSyncTimer;

    public BaseService() {
        mServiceSyncTimer = new Timer();
        mTelemetryTimer = new Timer();
    }

    /**
     * Текущий авторизованный пользователь
     * @return возвращается текущий авторизованный пользователь
     */
    protected BasicUser getBasicUser() {
        return Authorization.getInstance().getUser();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        onExceptionIntercept();

        if(getApplication() instanceof OnCoreApplicationListeners) {
            OnCoreApplicationListeners listeners = (OnCoreApplicationListeners)getApplication();
            listeners.addNotificationListener(this);
        }

        mNotificationAsyncTask = new NotificationAsyncTask(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(getBasicUser() != null) {
            runTracking(intent);
            runTelemetry(intent);

            mNotificationAsyncTask.execute(GlobalSettings.getConnectUrl());
        }
        return Service.START_STICKY;
    }

    /**
     * Интервал для перемещения обходчика
     */
    protected abstract int getTrackTimeoutInterval(Intent intent);

    protected void runTracking(Intent intent) {
        if (intent != null) {
            mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }

            String providerName = LocationUtil.getProviderName(mLocationManager, PreferencesManager.getInstance().getTrackLocation());
            if(providerName == null) {
                return;
            }
            mLocationManager.requestLocationUpdates(providerName, getTrackTimeoutInterval(intent), PreferencesManager.getInstance().getDistance(), mLocationListener);
        }
    }

    /**
     * Интервал для перемещения обходчика
     */
    protected abstract int getTelemetryInterval(Intent intent);

    protected void runTelemetry(Intent intent) {
        mDeviceInfoListener.run();

        if(intent != null) {
            mTelemetryTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mTelemetryListener.run();
                }
            }, 0, getTelemetryInterval(intent));
        }
    }

    /**
     * Обработчик перехвата ошибок
     */
    public void onExceptionIntercept() {
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler(), getExceptionGroup(), getExceptionCode(), this));
    }

    /**
     * Группа ошибки из IExceptionGroup
     * @return строка
     */
    public String getExceptionGroup(){
        return IExceptionGroup.SERVICE;
    }

    /**
     * Обработчик получения сообщения
     * @param type тип сообщения
     * @param buffer сообщение
     */
    @Override
    public void onPushMessage(String type, byte[] buffer) {
        StringMail[] mails = StringMail.getInstance(buffer);
        if (mails != null) {
            for(StringMail mail : mails) {
                NotificationUtil.showMessage(this, mail.c_message, getNotificationIntent(), (int)new Date().getTime());
            }
        }
    }

    public abstract Intent getNotificationIntent();

    /**
     * Обработчик. Сообщение доставлено
     * @param buffer сообщение
     */
    @Override
    public void onPushDelivered(byte[] buffer) {

    }

    /**
     * Обработчик. Сообщение не доставлено
     * @param buffer сообщение
     */
    @Override
    public void onPushUnDelivered(byte[] buffer) {

    }

    /**
     * Соединение с сервером установлено
     */
    @Override
    public void onConnect() {

    }

    /**
     * Регистрация на сервере произведена
     */
    @Override
    public void onRegistry() {

    }

    /**
     * Отключение от сервера
     */
    @Override
    public void onDisconnect() {

    }

    @Override
    public void onNotificationCount(Integer integer) {
        if(integer != null && integer > 0) {
            NotificationUtil.showMessage(this, "Доступно " + integer + " сообщений", getNotificationIntent(), NotificationUtil.CHANNEL_NOTIFY_ID);
        }
    }

    @Override
    public void onDestroy() {

        clear();

        super.onDestroy();
    }

    public void clear() {
        if(mNotificationAsyncTask != null) {
            mNotificationAsyncTask.cancel(true);
            mNotificationAsyncTask = null;
        }

        mServiceSyncTimer.cancel();

        if(mLocationManager != null) {
            mLocationManager.removeUpdates(mLocationListener);
        }

        if(mTelemetryTimer != null) {
            mTelemetryTimer.cancel();
            mTelemetryTimer.purge();
            mTelemetryTimer = null;
        }

        if(getApplication() instanceof OnCoreApplicationListeners) {
            OnCoreApplicationListeners listeners = (OnCoreApplicationListeners)getApplication();
            listeners.removeNotificationListener(this);
        }
    }
}
