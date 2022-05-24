package ru.mobnius.core.data.app;

import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.NetworkChangeReceiver;
import ru.mobnius.core.data.authorization.Authorization;
import ru.mobnius.core.data.configuration.DefaultPreferencesManager;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.exception.IExceptionCode;
import ru.mobnius.core.data.exception.IExceptionGroup;
import ru.mobnius.core.data.exception.MyUncaughtExceptionHandler;
import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.data.network.OnNetworkChangeListeners;
import ru.mobnius.core.data.socket.OnSocketListeners;
import ru.mobnius.core.data.socket.SocketManager;
import ru.mobnius.core.utils.HardwareUtil;

public abstract class Application extends android.app.Application
        implements OnCoreApplicationListeners {

    protected List<OnNetworkChangeListeners> mNetworkChangeListener;
    protected List<OnSocketListeners> mSocketNotificationListener;

    public Application() {
        mNetworkChangeListener = new ArrayList<>();
        mSocketNotificationListener = new ArrayList<>();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        onExceptionIntercept();

        DefaultPreferencesManager.createInstance(this, DefaultPreferencesManager.NAME);

        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(Intent.ACTION_AIRPLANE_MODE_CHANGED);

        Authorization.createInstance(this);
        // отслеживаем изменения подключения к сети интернет
        registerReceiver(new NetworkChangeReceiver(), filter);
    }

    @Override
    public void onAuthorized(int type) {
        BasicCredentials credentials = Authorization.getInstance().getUser().getCredentials();
        FileManager fileManager = FileManager.createInstance(credentials, this);

        // создаем директории для хранения изображений
        File dir = fileManager.getTempPictureFolder();
        if (!dir.exists()) {
            if(!dir.mkdirs()) {
                Logger.debug("Каталог " + dir.getPath() + " не создан");
            }
        }
        dir = fileManager.getAttachmentsFolder();
        if(!dir.exists()){
            if(!dir.mkdirs()) {
                Logger.debug("Каталог " + dir.getPath() + " не создан");
            }
        }

        if(SocketManager.getInstance() != null)
            SocketManager.getInstance().destroy();

        SocketManager socketManager = SocketManager.createInstance(GlobalSettings.getConnectUrl(), credentials, HardwareUtil.getNumber(this));
        socketManager.open(this);
    }

    @Override
    public void unAuthorized(boolean clearUserAuthorization) {
        if(clearUserAuthorization) {
            PreferencesManager.getInstance().setPinAuth(false);
            Authorization.getInstance().destroy();
        } else {
            Authorization.getInstance().reset();
        }
        FileManager.getInstance().destroy();
        SocketManager.getInstance().destroy();
        PreferencesManager.getInstance().destroy();
    }

    @Override
    public void onExceptionIntercept() {
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler(), getExceptionGroup(), getExceptionCode(), this));
    }

    @Override
    public String getExceptionGroup() {
        return IExceptionGroup.APPLICATION;
    }

    @Override
    public int getExceptionCode() {
        return IExceptionCode.ALL;
    }

    /**
     * Обработчик изменения подключения к сети
     *
     * @param isOnline приложение в онлайн
     * @param isSocketConnect сокет соединение
     * @param isServerExists подключение к серверу доступно.
     */
    @Override
    public void onNetworkChange(boolean isOnline, boolean isSocketConnect, boolean isServerExists) {
        if (mNetworkChangeListener != null) {
            for (OnNetworkChangeListeners change : mNetworkChangeListener) {
                if (change != null) {
                    change.onNetworkChange(isOnline, isSocketConnect, isServerExists);
                }
            }
        }
    }

    /**
     * Подписаться для добавления обработчика. Делать это в событии onStart
     *
     * @param change обработчик
     */
    public void addNetworkChangeListener(OnNetworkChangeListeners change) {
        mNetworkChangeListener.add(change);
    }

    /**
     * Подписаться для удаление обработчика. Делать это в событии onStop
     *
     * @param change обработчик
     */
    public void removeNetworkChangeListener(OnNetworkChangeListeners change) {
        if (mNetworkChangeListener != null) {
            mNetworkChangeListener.remove(change);
        }
    }

    /**
     * Подписаться для добавления обработчика. Делать это в событии onStart
     * @param notification обработчик
     */
    public void addNotificationListener(OnSocketListeners notification) {
        mSocketNotificationListener.add(notification);
    }

    /**
     * Подписаться для удаление обработчика. Делать это в событии onStop
     * @param notification обработчик
     */
    public void removeNotificationListener(OnSocketListeners notification) {
        if(mSocketNotificationListener != null) {
            mSocketNotificationListener.remove(notification);
        }
    }

    @Override
    public void onPushMessage(String type, byte[] buffer) {
        if(mSocketNotificationListener != null) {
            for(OnSocketListeners notification : mSocketNotificationListener) {
                notification.onPushMessage(type, buffer);
            }
        }
    }

    @Override
    public void onPushDelivered(byte[] buffer) {
        if(mSocketNotificationListener != null) {
            for(OnSocketListeners notification : mSocketNotificationListener) {
                notification.onPushDelivered(buffer);
            }
        }
    }

    @Override
    public void onPushUnDelivered(byte[] buffer) {
        if(mSocketNotificationListener != null) {
            for(OnSocketListeners notification : mSocketNotificationListener) {
                notification.onPushUnDelivered(buffer);
            }
        }
    }

    @Override
    public void onConnect() {

    }

    @Override
    public void onRegistry() {

    }

    @Override
    public void onDisconnect() {

    }
}
