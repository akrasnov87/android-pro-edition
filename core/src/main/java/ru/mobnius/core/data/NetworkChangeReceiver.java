package ru.mobnius.core.data;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import ru.mobnius.core.data.network.OnNetworkChangeListeners;
import ru.mobnius.core.data.network.ServerExistsAsyncTask;
import ru.mobnius.core.data.socket.SocketManager;
import ru.mobnius.core.utils.NetworkInfoUtil;

/**
 * отслеживание подключение к сети
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    private boolean isSocketConnect = false;
    private ServerExistsAsyncTask mTask;

    @Override
    public void onReceive(Context context, Intent intent) {
        SocketManager socketManager = SocketManager.getInstance();
        if(socketManager != null)
            isSocketConnect = socketManager.isConnected();

        boolean isOnline = NetworkInfoUtil.isNetworkAvailable(context);

        if(context instanceof OnNetworkChangeListeners) {
            if(mTask != null) {
                mTask.cancel(true);
                mTask = null;
            }

            OnNetworkChangeListeners networkChangeListeners = (OnNetworkChangeListeners)context;

            mTask = new ServerExistsAsyncTask(networkChangeListeners, isOnline, isSocketConnect);
            mTask.execute();
        }
    }
}
