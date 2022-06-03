package com.mobwal.android.library.data.sync.util.transfer;

import android.app.Activity;

import androidx.annotation.NonNull;

import java.util.Date;

import io.socket.emitter.Emitter;
import com.mobwal.android.library.data.sync.OnSynchronizationListeners;

/**
 * Слушатель для механизма передачи и получения данных
 */
public class TransferListeners
        implements Emitter.Listener {

    public static final int START = 0;
    public static final int RESTART = 1;
    public static final int PERCENT = 2;
    public static final int STOP = 3;
    public static final int END = 4;
    public static final int ERROR = 5;

    Activity activity;
    String tid;
    TransferStatusListeners mStatusListeners;
    Transfer transfer;
    private final OnSynchronizationListeners mListeners;
    protected Date iterationStartTime;

    /**
     * конструктор
     *
     * @param activity интерфейс
     * @param tid идентификатор транзакции
     * @param listeners статус
     */
    public TransferListeners(@NonNull OnSynchronizationListeners synchronization, @NonNull Activity activity, @NonNull String tid, @NonNull Transfer transfer, @NonNull TransferStatusListeners listeners) {
        this.activity = activity;
        this.tid = tid;
        this.mStatusListeners = listeners;
        this.transfer = transfer;
        this.mListeners = synchronization;
    }

    /**
     * Время начала итерации
     * @return время
     */
    public Date getIterationStartTime() {
        return iterationStartTime == null ? new Date() : iterationStartTime;
    }

    public void onStart(){
        onHandler(START, tid, transfer, null);
    }

    public void onRestart(){
        onHandler(RESTART, tid, transfer, null);
    }

    public void onPercent(double percent, TransferSpeed speed, long lastTime, TransferData transferData) {
        iterationStartTime = new Date();
        TransferProgress progress = new TransferProgress(percent, speed, transferData, lastTime);

        onHandler(PERCENT, tid, transfer, progress);
    }

    public void onStop(){
        onHandler(STOP, tid, transfer, null);
    }

    public void onEnd(byte[] bytes) {
        onPercent(100,
                new TransferSpeed(transfer.getChunk(), new Date().getTime() - getIterationStartTime().getTime()),
                0,
                new TransferData(bytes.length, bytes.length));
        onHandler(END, tid, transfer, bytes);
    }

    /**
     * обработчик ошибок
     * @param message текст сообщения
     */
    public void onError(final String message){
        onHandler(ERROR, tid, transfer, message);
    }

    @Override
    public void call(Object... args) {

    }

    private void onCallHandler(int type, String tid, Transfer transfer, Object data){
        if(mListeners.getEntities(tid).length > 0) {
            switch (type) {
                case START:
                    mStatusListeners.onStartTransfer(tid, transfer);
                    break;

                case RESTART:
                    mStatusListeners.onRestartTransfer(tid, transfer);
                    break;

                case PERCENT:
                    mStatusListeners.onPercentTransfer(tid, (TransferProgress) data, transfer);
                    break;

                case STOP:
                    mStatusListeners.onStopTransfer(tid, transfer);
                    break;

                case END:
                    mStatusListeners.onEndTransfer(tid, transfer, data);
                    break;

                case ERROR:
                    mStatusListeners.onErrorTransfer(tid, (String) data, transfer);
                    break;
            }
        }
    }

    private void onHandler(final int type, final String tid, final Transfer transfer, final Object data){
        if(activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    onCallHandler(type, tid, transfer, data);
                }
            });
        }else{
            onCallHandler(type, tid, transfer, data);
        }
    }

    /**
     * вычисление оставшегося времени
     * @param dtStart дата начала процесса
     * @param percent процент выполнения
     * @return время в милисекундах
     */
    protected long getLastTime(Date dtStart, int percent) {
        if(percent == 0)
            percent = 1;
        // прошло время с начала запуска
        long workTime = new Date().getTime() - dtStart.getTime();
        // приблизительная продолжительность
        long totalTime = (workTime * 100) / percent;

        return totalTime - workTime;
    }
}
