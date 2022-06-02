package ru.mobnius.core.srv;

import android.os.AsyncTask;

import java.io.IOException;

import ru.mobnius.core.data.NotificationManager;
import com.mobwal.android.library.authorization.Authorization;
import ru.mobnius.core.data.logger.Logger;

public class NotificationAsyncTask extends AsyncTask<String, Void, Integer> {
    private OnNotificationCountListeners mListeners;

    public NotificationAsyncTask(OnNotificationCountListeners listeners) {
        mListeners = listeners;
    }
    @Override
    protected Integer doInBackground(String... strings) {
        String token = Authorization.getInstance().getUser().getCredentials().getToken();
        String baseUrl = strings[0];
        NotificationManager notificationManager = new NotificationManager(baseUrl, token);
        try {
            int count = notificationManager.getNewMessageCount();
            notificationManager.sended();
            return count;
        } catch (IOException e) {
            Logger.error(e);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        if(mListeners != null) {
            mListeners.onNotificationCount(integer);
        }
    }

    public interface OnNotificationCountListeners {
        void onNotificationCount(Integer integer);
    }
}
