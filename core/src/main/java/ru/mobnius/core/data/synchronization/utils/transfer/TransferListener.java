package ru.mobnius.core.data.synchronization.utils.transfer;

import android.app.Activity;

import java.util.Date;

import io.socket.emitter.Emitter;
import ru.mobnius.core.data.synchronization.OnSynchronizationListeners;

/**
 * Слушатель для механизма передачи и получения данных
 */
public class TransferListener implements Emitter.Listener {

    public static final int START = 0;
    public static final int RESTART = 1;
    public static final int PERCENT = 2;
    public static final int STOP = 3;
    public static final int END = 4;
    public static final int ERROR = 5;

    Activity activity;
    String tid;
    ITransferStatusCallback statusCallback;
    Transfer transfer;
    private OnSynchronizationListeners synchronization;
    protected Date iterationStartTime;

    /**
     * конструктор
     *
     * @param activity интерфейс
     * @param tid идентификатор транзакции
     * @param statusCallback статус
     */
    public TransferListener(OnSynchronizationListeners synchronization, Activity activity, String tid, Transfer transfer, ITransferStatusCallback statusCallback){
        this.activity = activity;
        this.tid = tid;
        this.statusCallback = statusCallback;
        this.transfer = transfer;
        this.synchronization = synchronization;
    }

    /**
     * Время начала итерации
     * @return время
     */
    public Date getIterationStartTime(){
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
        TransferProgress progress = TransferProgress.getInstance(percent, speed, transferData, lastTime);

        onHandler(PERCENT, tid, transfer, progress);
    }

    public void onStop(){
        onHandler(STOP, tid, transfer, null);
    }

    public void onEnd(byte[] bytes){
        onPercent(100,
                TransferSpeed.getInstance(transfer.getChunk(), new Date().getTime() - getIterationStartTime().getTime()),
                0,
                TransferData.getInstance(bytes.length, bytes.length));
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
        if(synchronization.getEntities(tid).length > 0) {
            switch (type) {
                case START:
                    statusCallback.onStartTransfer(tid, transfer);
                    break;

                case RESTART:
                    statusCallback.onRestartTransfer(tid, transfer);
                    break;

                case PERCENT:
                    statusCallback.onPercentTransfer(tid, (TransferProgress) data, transfer);
                    break;

                case STOP:
                    statusCallback.onStopTransfer(tid, transfer);
                    break;

                case END:
                    statusCallback.onEndTransfer(tid, transfer, data);
                    break;

                case ERROR:
                    statusCallback.onErrorTransfer(tid, (String) data, transfer);
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
    protected long getLastTime(Date dtStart, int percent){
        if(percent == 0)
            percent = 1;
        // прошло время с начала запуска
        long workTime = new Date().getTime() - dtStart.getTime();
        // приблизительная продолжительность
        long totalTime = (workTime * 100) / percent;

        return totalTime - workTime;
    }
}
