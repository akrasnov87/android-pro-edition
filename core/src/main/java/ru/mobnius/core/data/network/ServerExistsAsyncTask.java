package ru.mobnius.core.data.network;

import android.os.AsyncTask;

import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.RequestManager;
import ru.mobnius.core.data.logger.Logger;

public class ServerExistsAsyncTask extends AsyncTask<Void, String, Boolean> {

    private final OnNetworkChangeListeners mListeners;
    private final boolean mIsSocketConnect;
    private final boolean mIsOnline;
    private final String mBaseUrl;

    public ServerExistsAsyncTask(OnNetworkChangeListeners listeners, boolean isOnline, boolean isSocketConnect) {
        mListeners = listeners;
        mBaseUrl = GlobalSettings.getConnectUrl();
        mIsSocketConnect = isSocketConnect;
        mIsOnline = isOnline;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        boolean serverExists = false;
        if (mIsOnline) {
            try {
                serverExists = RequestManager.exists(mBaseUrl) != null;
            } catch (Exception e) {
                Logger.error(e);
            }
        }
        return serverExists;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        if (mListeners != null) {
            mListeners.onNetworkChange(mIsOnline, mIsSocketConnect, aBoolean);
        }
    }
}