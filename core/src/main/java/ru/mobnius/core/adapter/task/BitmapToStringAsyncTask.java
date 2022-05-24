package ru.mobnius.core.adapter.task;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import ru.mobnius.core.utils.BitmapUtil;

public class BitmapToStringAsyncTask extends AsyncTask<Bitmap, Void, String> {

    private OnConvertFinishedListener mListener;

    public BitmapToStringAsyncTask(OnConvertFinishedListener listener) {
        mListener = listener;
    }

    @Override
    protected String doInBackground(Bitmap... bitmaps) {
        Bitmap resize = BitmapUtil.scaleToFitWidth(bitmaps[0], BitmapUtil.QUALITY_240p);
        return BitmapUtil.toBase64(resize, BitmapUtil.IMAGE_QUALITY);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        if(mListener != null) {
            mListener.onConvertFinished(s);
        }
    }

    public interface OnConvertFinishedListener {
        void onConvertFinished(String base64);
    }
}
