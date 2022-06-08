package com.mobwal.android.library.data.sync.util.transfer;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import org.json.JSONObject;

import java.util.Date;

import io.socket.client.Socket;
import com.mobwal.android.library.data.sync.OnSynchronizationListeners;
import com.mobwal.android.library.util.StringUtil;

public class DownloadTransfer extends Transfer {
    /**
     * слушатель загрузки данных на клиент
     */
    DownloadTransfer.DownloadListener downloadListener;

    /**
     * массив байтов с загруженными данными
     */
    byte[] downloadBytes;

    /**
     * текущая позиция при загрузке на клиент
     */
    int downloadPosition = 0;
    private Date dtStart;

    public DownloadTransfer(@NonNull OnSynchronizationListeners synchronization, @NonNull Socket socket, @NonNull String version, @NonNull String tid) {
        super(synchronization, socket, version, tid);
    }

    /**
     * загрузка на клиент
     * @param callback результат
     */
    public void download(@NonNull final TransferStatusListeners callback){
        dtStart = new Date();
        this.callback = callback;
        disconnectListener();

        Log.d(DOWNLOAD_TAG, "Старт иден. " + tid);
        downloadListener = new DownloadListener(synchronization, tid, this, callback);
        downloadListener.onStart();
        socket.on(EVENT_DOWNLOAD, downloadListener);


        Log.d(DOWNLOAD_TAG, "tid: "+tid+"; start: " + downloadPosition + "; chunk: " + getChunk());
        socket.emit(EVENT_DOWNLOAD, protocolVersion, downloadPosition, getChunk(), tid);
    }

    /**
     * перезапуск процесса
     */
    public void restart(){
        downloadListener = new DownloadTransfer.DownloadListener(synchronization, tid, this, callback);
        socket.on(EVENT_DOWNLOAD, downloadListener);

        socket.emit(EVENT_DOWNLOAD, protocolVersion, downloadPosition,  getChunk(), tid);
        Log.d(DOWNLOAD_TAG, "Запуск после восстановления "+ EVENT_DOWNLOAD +": " + downloadPosition);
    }

    /**
     * Удаление слушателя
     */
    public void removeListener() {
        if(socket != null){
            if(downloadListener != null) {
                socket.off(EVENT_DOWNLOAD, downloadListener);
                downloadListener = null;

                Log.d(DOWNLOAD_TAG, "Удаляем обработчик " + EVENT_DOWNLOAD);
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        downloadBytes = null;
        downloadPosition = 0;
        dtStart = null;
    }

    class DownloadListener extends TransferListeners {
        /**
         * конструктор
         *
         * @param tid            идентификатор транзакции
         * @param statusCallback статус
         */
        public DownloadListener(@NonNull OnSynchronizationListeners synchronization, @NonNull String tid, @NonNull DownloadTransfer transfer, @NonNull TransferStatusListeners statusCallback) {
            super(synchronization, tid, transfer, statusCallback);
        }

        /**
         * добавление элементов в массив
         * @param a первый
         * @param b второй
         * @return результат соединения массивов
         */
        byte[] f(byte[] a, byte[] b) {
            byte[] c = new byte[a.length + b.length];
            System.arraycopy(a, 0, c, 0, a.length);
            System.arraycopy(b, 0, c, a.length, b.length);
            return c;
        }

        @Override
        public void call(final Object... args) {
            if(synchronization.getEntities(tid).length > 0) {
                try {
                    processing(args);
                } catch (Exception e) {
                    onError(StringUtil.exceptionToString(e));
                }
            }
        }

        /**
         * обработка результат
         * @param args параметры
         */
        void processing(Object[] args){
            final boolean valid = args != null && args[0] instanceof JSONObject;

            if(!valid) {
                onError("Переданный объект не является JSONObject");
                return;
            }

            JSONObject jsonObject = (JSONObject) args[0];
            TransferResult result = TransferResult.readResult(jsonObject);
            if(result.tid.equals(tid)) {
                if (!result.data.success) {
                    onError(result.data.msg);
                } else {
                    if (downloadBytes == null) {
                        downloadBytes = new byte[0];
                    }

                    downloadBytes = f(downloadBytes, result.result);

                    if (result.meta.processed) {
                        Log.d(DOWNLOAD_TAG, "tid: " + tid + "; finish");
                        onEnd(downloadBytes);
                        transfer.destroy();
                    } else {
                        downloadPosition = result.meta.start;
                        int percent = (int)(((long)downloadPosition * 100) / (long)result.meta.totalLength);
                        Log.d(DOWNLOAD_TAG, "tid: " + tid + "; start: " + downloadPosition + "; chunk: " + getChunk() + "; totalLength: " + result.meta.totalLength);
                        socket.emit(EVENT_DOWNLOAD, protocolVersion, downloadPosition, getChunk(), tid);

                        long lashChunk = getChunk();

                        if (getIterationStartTime() != null) {
                            // время которое потребовалось для передачи CHUNK блока
                            long time = new Date().getTime() - getIterationStartTime().getTime();
                            if(time == 0)
                                time = 1;
                            // сколько нужно блоков для передачи за 1 секунду?
                            updateChunk(((long) INTERVAL * getChunk()) / time);

                            onPercent(percent,
                                    new TransferSpeed(lashChunk, new Date().getTime() - getIterationStartTime().getTime()),
                                    getLastTime(dtStart, percent),
                                    new TransferData(downloadPosition, result.meta.totalLength));
                        }else {
                            onPercent(percent,
                                    new TransferSpeed(downloadPosition, new Date().getTime() - dtStart.getTime()),
                                    getLastTime(dtStart, percent),
                                    new TransferData(downloadPosition, result.meta.totalLength));
                        }
                    }
                }
            }
        }
    }
}
