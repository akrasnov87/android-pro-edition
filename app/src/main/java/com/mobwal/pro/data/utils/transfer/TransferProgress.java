package com.mobwal.pro.data.utils.transfer;

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
    private double percent;
    /**
     * скорость передачи данных
     */
    private TransferSpeed speed;
    /**
     * информация о переданных данных
     */
    private TransferData transferData;
    /**
     * Время оставшее до завершения
     */
    private long time;

    private TransferProgress(){}

    public double getPercent(){
        return percent;
    }

    public TransferData getTransferData(){
        return transferData;
    }

    public static TransferProgress getInstance(double percent, TransferSpeed speed, TransferData transferData, long time){
        TransferProgress progress = new TransferProgress();
        progress.speed = speed;
        progress.percent = percent;
        progress.time = time;
        progress.transferData = transferData;

        return progress;
    }

    @NonNull
    @Override
    public String toString() {
        if(getPercent() >= 100) {
            return "обработка данных...";
        } else {
            Date date = new Date(time);
            DateFormat formatter = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
            return "~" + formatter.format(date) + "(" + speed.toString() + ")";
        }
    }
}
