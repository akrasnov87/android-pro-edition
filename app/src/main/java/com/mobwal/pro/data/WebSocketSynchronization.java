package com.mobwal.pro.data;

import android.app.Activity;

import com.mobwal.pro.WalkerSQLContext;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import io.socket.client.Socket;
import io.socket.client.SocketIOException;
import io.socket.emitter.Emitter;
import io.socket.engineio.client.EngineIOException;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.data.socket.SocketManager;

import com.mobwal.pro.data.utils.transfer.DownloadTransfer;
import com.mobwal.pro.data.utils.transfer.ITransferStatusCallback;
import com.mobwal.pro.data.utils.transfer.Transfer;
import com.mobwal.pro.data.utils.transfer.TransferListener;
import com.mobwal.pro.data.utils.transfer.TransferProgress;
import com.mobwal.pro.data.utils.transfer.UploadTransfer;
import com.mobwal.pro.utilits.SyncUtil;

import ru.mobnius.core.utils.DoubleUtil;

/**
 * Механизм обработки синхронизации через websocket
 */
public abstract class WebSocketSynchronization extends BaseSynchronization {
    /**
     * обработчик ответов от websocket
     */
    private WebSocketSynchronization.SynchronizationListener synchronizationListener;
    private SynchronizationConnectListener synchronizationConnectListener;

    /**
     * хранилище для объектов по приемке и отдаче пакетов
     */
    private HashMap<String, Transfer> transfers;
    private List<EndTransferResult> mEndTransferResults;

    /**
     * конструктор
     * @param name имя
     */
    public WebSocketSynchronization(WalkerSQLContext context, String name, boolean zip) {
        super(context, name, zip);
        transfers = new HashMap<>();
        mEndTransferResults = new ArrayList<>();
    }

    /**
     * Сокет соединение с сервером
     * @return текущее соединение
     */
    public SocketManager getSocketManager() {
        return SocketManager.getInstance();
    }

    @Override
    public void start(Activity activity, IProgress progress) {
        super.start(activity, progress);

        onProgress(IProgressStep.START, "Проверка подключения к серверу.", null);

        if(getSocketManager() != null && getSocketManager().isRegistered()) {
            synchronizationListener = new WebSocketSynchronization.SynchronizationListener();
            synchronizationConnectListener = new SynchronizationConnectListener();

            //getSocketManager().getSocket().on(Socket.EVENT_RECONNECT_FAILED, synchronizationConnectListener);
            //getSocketManager().getSocket().on(Socket.EVENT_RECONNECTING, synchronizationConnectListener);
            //getSocketManager().getSocket().on(Socket.EVENT_ERROR, synchronizationConnectListener);
            getSocketManager().getSocket().on(Socket.EVENT_DISCONNECT, synchronizationConnectListener);
            //getSocketManager().getSocket().on(Socket.EVENT_RECONNECT, synchronizationConnectListener);
            //getSocketManager().getSocket().on(Socket.EVENT_RECONNECT_ATTEMPT, synchronizationConnectListener);
            //getSocketManager().getSocket().on(Socket.EVENT_RECONNECT_ERROR, synchronizationConnectListener);
            getSocketManager().getSocket().on(Socket.EVENT_CONNECT, synchronizationConnectListener);
            //getSocketManager().getSocket().on(Socket.EVENT_CONNECT_TIMEOUT, synchronizationConnectListener);
            getSocketManager().getSocket().on(Socket.EVENT_CONNECT_ERROR, synchronizationConnectListener);

            getSocketManager().getSocket().on("synchronization", synchronizationListener);
            getSocketManager().getSocket().on("synchronization-status", synchronizationListener);

            // устанавливаем идентификаторы
            for(Entity entity : getEntityToList()){
                onProgress(IProgressStep.START, "select " + entity.tableName + " change=" + DoubleUtil.toStringValue(entity.change), entity.tid);

                if (!SyncUtil.updateTid(this, entity.tableName, entity.tid)) {
                    onError(IProgressStep.START, "Ошибка обнуления tid для таблицы " + entity.tableName, entity.tid);
                    stop();
                    return;
                }
            }
        }else{
            onError(IProgressStep.START, "Подключение к серверу по websocket не доступно.", null);
            stop();
        }
    }

    @Override
    protected Object sendBytes(final String tid, byte[] bytes) {
        if(bytes != null && getSocketManager() != null) {
            onProgress(IProgressStep.UPLOAD, "", tid);
            UploadTransfer uploadTransfer = new UploadTransfer(this, getSocketManager().getSocket(), PreferencesManager.SYNC_PROTOCOL_v2, getActivity(), tid);
            transfers.put(tid, uploadTransfer);

            uploadTransfer.upload(bytes, new ITransferStatusCallback() {
                @Override
                public void onStartTransfer(String tid, Transfer transfer) {
                    onProgressTransfer(TransferListener.START, tid, transfer, null);
                }

                @Override
                public void onRestartTransfer(String tid, Transfer transfer) {
                    onProgressTransfer(TransferListener.RESTART, tid, transfer, null);
                }

                @Override
                public void onPercentTransfer(String tid, TransferProgress progress, Transfer transfer) {
                    onProgressTransfer(TransferListener.PERCENT, tid, transfer, progress);
                }

                @Override
                public void onStopTransfer(String tid, Transfer transfer) {
                    onProgressTransfer(TransferListener.STOP, tid, transfer, null);
                }

                @Override
                public void onEndTransfer(String tid, Transfer transfer, Object data) {
                    onProgressTransfer(TransferListener.END, tid, transfer, data);
                    getSocketManager().getSocket().emit("synchronization", tid, PreferencesManager.SYNC_PROTOCOL_v2);
                }

                @Override
                public void onErrorTransfer(String tid, String message, Transfer transfer) {
                    onProgressTransfer(TransferListener.ERROR, tid, transfer, message);
                    onError(IProgressStep.UPLOAD, message, tid);
                }
            });
        }

        return null;
    }

    @Override
    protected Object sendBytes(final String tid, byte[] bytes, FileTransferFinishedCallback fileTransferFinishedCallback) {
        if(bytes != null && getSocketManager() != null) {
            onProgress(IProgressStep.UPLOAD, "", tid);
            UploadTransfer uploadTransfer = new UploadTransfer(this, getSocketManager().getSocket(), PreferencesManager.SYNC_PROTOCOL_v2, getActivity(), tid);
            transfers.put(tid, uploadTransfer);

            uploadTransfer.upload(bytes, new ITransferStatusCallback() {
                @Override
                public void onStartTransfer(String tid, Transfer transfer) {
                    onProgressTransfer(TransferListener.START, tid, transfer, null);
                }

                @Override
                public void onRestartTransfer(String tid, Transfer transfer) {
                    onProgressTransfer(TransferListener.RESTART, tid, transfer, null);
                }

                @Override
                public void onPercentTransfer(String tid, TransferProgress progress, Transfer transfer) {
                    onProgressTransfer(TransferListener.PERCENT, tid, transfer, progress);
                }

                @Override
                public void onStopTransfer(String tid, Transfer transfer) {
                    onProgressTransfer(TransferListener.STOP, tid, transfer, null);
                }

                @Override
                public void onEndTransfer(String tid, Transfer transfer, Object data) {
                    onProgressTransfer(TransferListener.END, tid, transfer, data);
                    getSocketManager().getSocket().emit("synchronization", tid, PreferencesManager.SYNC_PROTOCOL_v2);
                    fileTransferFinishedCallback.onFileTransferFinish();
                }

                @Override
                public void onErrorTransfer(String tid, String message, Transfer transfer) {
                    onProgressTransfer(TransferListener.ERROR, tid, transfer, message);
                    onError(IProgressStep.UPLOAD, message, tid);
                }
            });
        }

        return null;
    }

    @Override
    public void stop() {
        if(synchronizationListener != null && getSocketManager() != null) {
            getSocketManager().getSocket().off("synchronization", synchronizationListener);
            getSocketManager().getSocket().off("synchronization-status", synchronizationListener);
        }

        if(synchronizationConnectListener != null && getSocketManager() != null){
            //getSocketManager().getSocket().off(Socket.EVENT_RECONNECT_FAILED, synchronizationConnectListener);
            //getSocketManager().getSocket().off(Socket.EVENT_RECONNECTING, synchronizationConnectListener);
            //getSocketManager().getSocket().off(Socket.EVENT_ERROR, synchronizationConnectListener);
            getSocketManager().getSocket().off(Socket.EVENT_DISCONNECT, synchronizationConnectListener);
            //getSocketManager().getSocket().off(Socket.EVENT_RECONNECT, synchronizationConnectListener);
            //getSocketManager().getSocket().off(Socket.EVENT_RECONNECT_ATTEMPT, synchronizationConnectListener);
            //getSocketManager().getSocket().off(Socket.EVENT_RECONNECT_ERROR, synchronizationConnectListener);

            getSocketManager().getSocket().off(Socket.EVENT_CONNECT, synchronizationConnectListener);
            //getSocketManager().getSocket().off(Socket.EVENT_CONNECT_TIMEOUT, synchronizationConnectListener);
            getSocketManager().getSocket().off(Socket.EVENT_CONNECT_ERROR, synchronizationConnectListener);
        }

        if(transfers != null){
            for(Transfer t : transfers.values()){
                t.destroy();
            }
            transfers.clear();
        }
        if(mEndTransferResults != null) {
            mEndTransferResults.clear();
        }

        super.stop();
    }

    /**
     * обработчик транспортировки данных
     * @param part тип операции
     * @param tid идентификатор транзакции
     * @param transfer объект транспортировки
     * @param data данные
     */
    protected void onProgressTransfer(final int part, final String tid, final Transfer transfer, final Object data) {
        if(progressListener != null) {
            if(getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(progressListener != null) {
                            switch (part) {
                                case TransferListener.START:
                                    progressListener.onStartTransfer(tid, transfer);
                                    break;

                                case TransferListener.RESTART:
                                    progressListener.onRestartTransfer(tid, transfer);
                                    break;

                                case TransferListener.PERCENT:
                                    progressListener.onPercentTransfer(tid, (TransferProgress) data, transfer);
                                    break;

                                case TransferListener.STOP:
                                    progressListener.onStopTransfer(tid, transfer);
                                    break;

                                case TransferListener.END:
                                    progressListener.onEndTransfer(tid, transfer, data);
                                    break;

                                case TransferListener.ERROR:
                                    progressListener.onErrorTransfer(tid, (String) data, transfer);
                                    break;
                            }
                        }
                    }
                });
            } else {
                switch (part) {
                    case TransferListener.START:
                        progressListener.onStartTransfer(tid, transfer);
                        break;

                    case TransferListener.RESTART:
                        progressListener.onRestartTransfer(tid, transfer);
                        break;

                    case TransferListener.PERCENT:
                        progressListener.onPercentTransfer(tid, (TransferProgress) data, transfer);
                        break;

                    case TransferListener.STOP:
                        progressListener.onStopTransfer(tid, transfer);
                        break;

                    case TransferListener.END:
                        progressListener.onEndTransfer(tid, transfer, data);
                        break;

                    case TransferListener.ERROR:
                        progressListener.onErrorTransfer(tid, (String) data, transfer);
                        break;
                }
            }
        }
    }

    /**
     * текущая синхронизация. Нужно для вызова в других контекстах
     * @return текущая синхронизация
     */
    private OnSynchronizationListeners getSynchronization(){
        return this;
    }

    @Override
    public void destroy() {
        super.destroy();
        mEndTransferResults = null;
        transfers = null;
        synchronizationListener = null;
    }

    class SynchronizationConnectListener implements Emitter.Listener {

        @Override
        public void call(final Object... args) {
            /*
            тут дубликат кода для того, чтобы можно было отслеживать статус синхронизации для экрана
            Если это служба для результат нам не важен
             */
            /*if(getActivity() != null){
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processing(args);
                        }catch (Exception e){
                            Logger.error(e);
                            onError(IProgressStep.UPLOAD, e, null);
                        }
                    }
                });
            }else {*/
                try {
                    processing(args);
                }catch (Exception e){
                    Logger.error(e);
                    onError(IProgressStep.UPLOAD, e, null);
                }
            //}
        }
        /**
         * обработка результат
         * @param args параметры
         */
        void processing(Object[] args) {
            if(args != null && args.length > 0){
                Object item = args[0];
                if(item instanceof String){
                    onProgress(IProgressStep.NONE, (String)item, null);
                }else if(item instanceof SocketIOException){
                    onError(IProgressStep.NONE, ((SocketIOException)item).getMessage(), null);
                } else if(item instanceof EngineIOException){
                    onError(IProgressStep.NONE, ((EngineIOException)item).getMessage(), null);
                } else if(item instanceof Integer){
                    onProgress(IProgressStep.NONE, String.valueOf(item), null);
                }
            }else {
                onProgress(IProgressStep.NONE, "ERROR", null);
            }
        }
    }

    /**
     * обработкчик socket
     */
    class SynchronizationListener implements Emitter.Listener {

        @Override
        public void call(final Object... args) {
            /*
            тут дубликат кода для того, чтобы можно было отслеживать статус синхронизации для экрана
            Если это служба для результат нам не важен
             */
            /*if(getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            processing(args);
                        }catch (Exception e){
                            Logger.error(e);
                            onError(IProgressStep.UPLOAD, e, null);
                        }
                    }
                });
            }else {*/
                try {
                    processing(args);
                }catch (Exception e){
                    Logger.error(e);
                    onError(IProgressStep.UPLOAD, e, null);
                }
            //}
        }

        /**
         * обработка результат
         * @param args параметры
         */
        void processing(Object[] args){
            final boolean valid = args != null && args[0] instanceof JSONObject;
            final String errorMsg = "Переданный объект не является JSONObject";

            if(!valid) {
                onError(IProgressStep.RESTORE, errorMsg, null);
                return;
            }

            JSONObject jsonObject = (JSONObject) args[0];
            try {
                // данный кусок кода нужен для вывода сообщений от сервера см. modules/synchronization/v1.js метод socketSend
                String tid = jsonObject.getString("tid");
                if(getSynchronization().getEntities(tid).length > 0) {
                    onProgress(IProgressStep.UPLOAD, jsonObject.getString("result"), tid);
                }
                return;
            } catch (JSONException ignored) {

            }

            JSONObject jsonData;
            try {
                jsonData = jsonObject.getJSONObject("data");
                if(jsonData.getBoolean("success")){
                    // тут нужно запросить данные от сервера
                    JSONObject metaJSONObject = jsonObject.getJSONObject("meta");
                    String tid = metaJSONObject.getString("tid");
                    if(metaJSONObject.getBoolean("processed") && getEntities(tid).length > 0) {
                        DownloadTransfer downloadTransfer = new DownloadTransfer(getSynchronization(), getSocketManager().getSocket(), PreferencesManager.SYNC_PROTOCOL_v2, getActivity(), tid);
                        if(transfers.get(tid) != null) {
                            Objects.requireNonNull(transfers.get(tid)).destroy();
                        }
                        transfers.put(tid, downloadTransfer);

                        downloadTransfer.download(new ITransferStatusCallback() {

                            @Override
                            public void onStartTransfer(String tid, Transfer transfer) {
                                onProgressTransfer(TransferListener.START, tid, transfer, null);
                            }

                            @Override
                            public void onRestartTransfer(String tid, Transfer transfer) {
                                onProgressTransfer(TransferListener.RESTART, tid, transfer, null);
                            }

                            @Override
                            public void onPercentTransfer(String tid, TransferProgress progress, Transfer transfer) {
                                onProgressTransfer(TransferListener.PERCENT, tid, transfer, progress);
                            }

                            @Override
                            public void onStopTransfer(String tid, Transfer transfer) {
                                onProgressTransfer(TransferListener.STOP, tid, transfer, null);
                            }

                            @Override
                            public void onEndTransfer(final String tid, final Transfer transfer, final Object data) {
                                mEndTransferResults.add(new EndTransferResult(tid, transfer, data));
                                // значит все пакеты приняты и нужно их обработать
                                if(mEndTransferResults.size() == transfers.size()) {
                                    Thread thread = new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            try {
                                                for (EndTransferResult result : mEndTransferResults) {
                                                    processingPackage(getCollectionTid(), (byte[]) result.mObject);
                                                    onProgressTransfer(TransferListener.END, result.mTid, result.mTransfer, null);
                                                    if (isEntityFinished()) {

                                                        if (getActivity() != null) {
                                                            getActivity().runOnUiThread(new Runnable() {
                                                                @Override
                                                                public void run() {
                                                                    stop();
                                                                }
                                                            });
                                                        } else {
                                                            stop();
                                                        }
                                                    }
                                                }
                                            }catch (ConcurrentModificationException ignored) {

                                            }
                                        }
                                    });
                                    thread.start();
                                }
                            }

                            @Override
                            public void onErrorTransfer(String tid, String message, Transfer transfer) {
                                onProgressTransfer(TransferListener.ERROR, tid, transfer, message);
                                onError(IProgressStep.DOWNLOAD, message, tid);
                            }
                        });
                    }
                }else{
                    // тут текст ошибки
                    onError(IProgressStep.RESTORE, jsonData.getString("msg"), null);
                    stop();
                }
            } catch (JSONException e) {
                onError(IProgressStep.RESTORE, e, null);
                stop();
            }
        }
    }
}
