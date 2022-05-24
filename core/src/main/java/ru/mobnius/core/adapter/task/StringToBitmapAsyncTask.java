package ru.mobnius.core.adapter.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import ru.mobnius.core.utils.BitmapUtil;

public class StringToBitmapAsyncTask extends AsyncTask<String, Void, Bitmap> {

    private OnConvertFinishedListener mListener;

    public StringToBitmapAsyncTask(OnConvertFinishedListener listener) {
        mListener = listener;
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        return BitmapUtil.toBitmap(strings[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        if(mListener != null) {
            mListener.onConvertFinished(bitmap);
        }
    }

    public interface OnConvertFinishedListener {
        void onConvertFinished(Bitmap bitmap);
    }
}
