package com.mobwal.android.library.data.sync.util.transfer;

import android.app.Activity;

import com.mobwal.android.library.data.sync.OnSynchronizationListeners;
import com.mobwal.android.library.socket.SocketManager;
import com.mobwal.android.library.util.StringUtil;

import io.socket.client.Socket;


public abstract class Transfer {

    final String UPLOAD_TAG = "UPLOAD_TRANSFER";
    final String DOWNLOAD_TAG = "DOWNLOAD_TRANSFER";

    final String EVENT_UPLOAD = "upload";
    final String EVENT_DOWNLOAD = "download";

    /**
     * размер передоваемых данных
     */
    // TODO: должно быть вычисляемым
    public static int CHUNK = 1024;
    public static boolean STATUS_TRANSFER_SPEED = false;

    /**
     * вычисленный размер блоков для передачи за секунду
     */
    private long calcChunk = CHUNK;

    /**
     * интервал в течении которого происходят вычисления
     */
    public static int INTERVAL = 1000;

    /**
     * Подключение через websocket
     */
    Socket socket;

    /**
     * версия протокола синхронизации
     */
    String protocolVersion;

    /**
     * слушатель "регистрации" пользователя на сервере
     */
    TransferRegistryListener transferRegistryListener;

    /**
     * слушатель потери соединения с сервером
     */
    TransferDisconnectListener transferDisconnectListener;

    /**
     * обработчик обратного вызова
     */
    TransferStatusListeners callback;

    /**
     * идентификатор транзакции
     */
    String tid;

    /**
     * имя выполняемой команды
     */
    String transferName;

    protected OnSynchronizationListeners synchronization;

    /**
     * конструктор
     * @param synchronization текущая синхронизация в рамках которой выполняется процесс
     * @param socket сокет соединение
     * @param version версия синхронизации
     * @param tid идентификатор транзакции
     */
    public Transfer(OnSynchronizationListeners synchronization, Socket socket, String version, String tid){
        this.socket = socket;
        protocolVersion = version;
        this.synchronization = synchronization;
        this.tid = tid;

        transferName = getClass().getSimpleName();
    }

    /**
     * Настройка слушителя отсуствия соединения с сервером
     */
    protected void disconnectListener() {
        removeDisconnectListener();

        transferDisconnectListener = new TransferDisconnectListener(tid, this, callback);
        transferRegistryListener = new TransferRegistryListener(tid, this, callback);

        socket.on(Socket.EVENT_DISCONNECT, transferDisconnectListener);
        socket.on(SocketManager.EVENT_REGISTRY, transferRegistryListener);
    }

    /**
     * Удаление слушителя о соединении с сервером
     */
    private void removeDisconnectListener(){
        if(socket != null){
            if(transferRegistryListener != null){
                socket.off(SocketManager.EVENT_REGISTRY, transferRegistryListener);
                transferRegistryListener = null;
            }

            if(transferDisconnectListener != null){
                socket.off(Socket.EVENT_DISCONNECT, transferDisconnectListener);
                transferDisconnectListener = null;
            }
        }
    }

    /**
     * Размер блока для отправки на сервер
     * @return возвращается размер
     */
    protected int getChunk() {
        if(STATUS_TRANSFER_SPEED) {
            return CHUNK;
        }else {
            return (int) calcChunk;
        }
    }

    /**
     * обновление размера блока
     * @param chunk размер блока
     */
    protected void updateChunk(long chunk){
        if(chunk > 0) {
            calcChunk = chunk;
        }else{
            if(calcChunk + chunk > 0){
                calcChunk = calcChunk + chunk;
            }
        }
    }

    /**
     * очистка данных
     */
    public void destroy() {
        removeDisconnectListener();
        removeListener();
    }

    /**
     * удаление слушателя
     */
    abstract void removeListener();

    /**
     * перезапуск процесса
     */
    abstract void restart();

    /**
     * обработчик подключения (регистрации на) к серверу
     */
    class TransferRegistryListener extends TransferListeners {
        /**
         * конструктор
         *
         * @param tid            идентификатор транзакции
         * @param statusCallback статус
         */
        public TransferRegistryListener(String tid, Transfer transfer, TransferStatusListeners statusCallback) {
            super(synchronization, tid, transfer, statusCallback);
        }

        @Override
        public void call(Object... args) {
            try{
                onRestart();
                transfer.restart();
            }catch (Exception e) {
                onError(StringUtil.exceptionToString(e));
            }
        }
    }

    /**
     * обработчик потери подключения к серверу
     */
    class TransferDisconnectListener extends TransferListeners {
        /**
         * конструктор
         *
         * @param tid            идентификатор транзакции
         * @param statusCallback статус
         */
        public TransferDisconnectListener(String tid, Transfer transfer, TransferStatusListeners statusCallback) {
            super(synchronization, tid, transfer, statusCallback);
        }

        @Override
        public void call(Object... args) {
            try{
                onStop();
                transfer.removeListener();
            }catch (Exception e) {
                onError(StringUtil.exceptionToString(e));
            }
        }
    }
}
