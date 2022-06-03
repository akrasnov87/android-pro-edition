package com.mobwal.android.library.data.sync.util.transfer;

import android.content.Context;

import androidx.annotation.NonNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

/**
 * статус передачи данных
 */
public class TransferProgress {
    /**
     * процент выполнения
     */
    private final double mPercent;

    /**
     * скорость передачи данных
     */
    private final TransferSpeed mSpeed;

    /**
     * информация о переданных данных
     */
    private final TransferData mTransferData;

    /**
     * Время оставшее до завершения
     */
    private final long mTime;

    public TransferProgress(double percent, @NonNull TransferSpeed speed, @NonNull TransferData transferData, long time) {
        this.mSpeed = speed;
        this.mPercent = percent;
        this.mTime = time;
        this.mTransferData = transferData;
    }

    public double getPercent(){
        return mPercent;
    }

    public TransferData getTransferData(){
        return mTransferData;
    }

    @NonNull
    public String toString(Context context) {
        if(getPercent() >= 100) {
            return "обработка данных...";
        } else {
            Date date = new Date(mTime);
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return "~" + formatter.format(date) + "(" + mSpeed.toString(context) + ")";
        }
    }
}
