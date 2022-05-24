package ru.mobnius.core.data;

import android.os.AsyncTask;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.utils.StreamUtil;

public class DownloadImageAsyncTask extends AsyncTask<String, Void, byte[]> {
    private OnImageLoadedListener mListener;

    public DownloadImageAsyncTask(OnImageLoadedListener listener) {
        mListener = listener;
    }

    @Override
    protected byte[] doInBackground(String... strings) {
        InputStream inputStream = getInputStream(strings[0]);
        try {
            return StreamUtil.readBytes(inputStream);
        } catch (Exception e) {
            Logger.error(e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(byte[] buffer) {
        if(mListener != null) {
            mListener.onImageLoaded(buffer);
        }
    }

    private InputStream getInputStream(String imageUrl) {
        URL url;
        try {
            url = new URL(imageUrl);
            HttpURLConnection connection;
            connection = (HttpURLConnection) url.openConnection();
            connection.connect();
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                return connection.getInputStream();
            }
            return null;
        } catch (IOException e) {
            Logger.error(e);
            return null;
        }
    }

    public interface OnImageLoadedListener {
        void onImageLoaded(byte[] buffer);
    }
}
