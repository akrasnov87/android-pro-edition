package ru.mobnius.core.data.synchronization.utils.transfer;

import android.app.Activity;


import io.socket.client.Socket;
import ru.mobnius.core.data.socket.SocketManager;
import ru.mobnius.core.data.synchronization.OnSynchronizationListeners;

import static ru.mobnius.core.data.GlobalSettings.STATUS_TRANSFER_SPEED;

public abstract class Transfer {

    final String UPLOAD_TAG = "UPLOAD_TRANSFER";
    final String DOWNLOAD_TAG = "DOWNLOAD_TRANSFER";

    final String EVENT_UPLOAD = "upload";
    final String EVENT_DOWNLOAD = "download";

    /**
     * размер передоваемых данных
     */
    // TODO: должно быть вычисляемым
    private final int CHUNK = 1024;

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
     * текущая активность
     */
    Activity context;

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
    ITransferStatusCallback callback;

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
     * @param context интерфейс
     * @param tid идентификатор транзакции
     */
    public Transfer(OnSynchronizationListeners synchronization, Socket socket, String version, Activity context, String tid){
        this.socket = socket;
        this.context = context;
        protocolVersion = version;
        this.synchronization = synchronization;
        this.tid = tid;

        transferName = getClass().getSimpleName();
    }

    /**
     * Настройка слушителя отсуствия соединения с сервером
     */
    protected void disconnectListener(){
        removeDisconnectListener();

        transferDisconnectListener = new TransferDisconnectListener(context, tid, this, callback);
        transferRegistryListener = new TransferRegistryListener(context, tid, this, callback);

        //socket.on(Socket.EVENT_DISCONNECT, transferDisconnectListener);
        //socket.on(SocketManager.EVENT_REGISTRY, transferRegistryListener);
    }

    /**
     * Удаление слушителя о соединении с сервером
     */
    private void removeDisconnectListener(){
        if(socket != null){
            if(transferRegistryListener != null){
                //socket.off(SocketManager.EVENT_REGISTRY, transferRegistryListener);
                transferRegistryListener = null;
            }

            if(transferDisconnectListener != null){
                //socket.off(Socket.EVENT_DISCONNECT, transferDisconnectListener);
                transferDisconnectListener = null;
            }
        }
    }

    /**
     * Размер блока для отправки на сервер
     * @return возвращается размер
     */
    protected int getChunk(){
        if(STATUS_TRANSFER_SPEED){
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
        if(chunk > 0){
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
    public void destroy(){
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
    class TransferRegistryListener extends TransferListener {
        /**
         * конструктор
         *
         * @param activity       интерфейс
         * @param tid            идентификатор транзакции
         * @param statusCallback статус
         */
        public TransferRegistryListener(Activity activity, String tid, Transfer transfer, ITransferStatusCallback statusCallback) {
            super(synchronization, activity, tid, transfer, statusCallback);
        }

        @Override
        public void call(Object... args) {
            try{
                onRestart();
                transfer.restart();
            }catch (Exception e){
                onError(e.getMessage());
            }
        }
    }

    /**
     * обработчик потери подключения к серверу
     */
    class TransferDisconnectListener extends TransferListener {
        /**
         * конструктор
         *
         * @param activity       интерфейс
         * @param tid            идентификатор транзакции
         * @param statusCallback статус
         */
        public TransferDisconnectListener(Activity activity, String tid, Transfer transfer, ITransferStatusCallback statusCallback) {
            super(synchronization, activity, tid, transfer, statusCallback);
        }

        @Override
        public void call(Object... args) {
            try{
                onStop();
                transfer.removeListener();
            }catch (Exception e){
                onError(e.getMessage());
            }
        }
    }
}
