package ru.mobnius.core.data.app;

import ru.mobnius.core.data.exception.IExceptionIntercept;
import ru.mobnius.core.data.network.OnNetworkChangeListeners;
import ru.mobnius.core.data.socket.OnSocketListeners;

public interface OnCoreApplicationListeners
        extends OnNetworkChangeListeners,
        IExceptionIntercept, OnSocketListeners {

    void onAuthorized(int type);
    void unAuthorized(boolean clearUserAuthorization);
    void addNetworkChangeListener(OnNetworkChangeListeners change);
    void removeNetworkChangeListener(OnNetworkChangeListeners change);
    void addNotificationListener(OnSocketListeners notification);
    void removeNotificationListener(OnSocketListeners notification);
    void onResetSetting();
}
