package com.mobwal.android.library.data.sync;

import androidx.annotation.NonNull;

import com.mobwal.android.library.data.sync.util.transfer.DownloadTransfer;
import com.mobwal.android.library.data.sync.util.transfer.Transfer;
import com.mobwal.android.library.data.sync.util.transfer.TransferListeners;
import com.mobwal.android.library.data.sync.util.transfer.TransferProgress;
import com.mobwal.android.library.data.sync.util.transfer.TransferStatusListeners;
import com.mobwal.android.library.data.sync.util.transfer.UploadTransfer;
import com.mobwal.android.library.sql.SQLContext;
import com.mobwal.android.library.util.DoubleUtil;
import com.mobwal.android.library.util.LogUtilSingleton;

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
import com.mobwal.android.library.socket.SocketManager;

import com.mobwal.android.library.util.StringUtil;
import com.mobwal.android.library.util.SyncUtil;

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
    public WebSocketSynchronization(SQLContext context, String name, boolean zip) {
        super(context, name, zip);
        transfers = new HashMap<>();
        mEndTransferResults = new ArrayList<>();
    }

    @Override
    public void start(@NonNull SocketManager socketManager, @NonNull ProgressListeners progress) {
        super.start(socketManager, progress);

        onProgress(ProgressStep.START, "Проверка подключения к серверу.", null);

        if(socketManager.isRegistered()) {
            synchronizationListener = new WebSocketSynchronization.SynchronizationListener();
            synchronizationConnectListener = new SynchronizationConnectListener();

            socketManager.getSocket().on(Socket.EVENT_DISCONNECT, synchronizationConnectListener);
            socketManager.getSocket().on(Socket.EVENT_CONNECT, synchronizationConnectListener);
            socketManager.getSocket().on(Socket.EVENT_CONNECT_ERROR, synchronizationConnectListener);

            socketManager.getSocket().on("synchronization", synchronizationListener);
            socketManager.getSocket().on("synchronization-status", synchronizationListener);

            // устанавливаем идентификаторы
            for(Entity entity : getEntityToList()){
                onProgress(ProgressStep.START, "select " + entity.tableName + " change=" + DoubleUtil.toStringValue(entity.change), entity.tid);

                if (!SyncUtil.updateTid(this, entity.tableName, entity.tid)) {
                    onError(ProgressStep.START, "Ошибка обнуления tid для таблицы " + entity.tableName, entity.tid);
                    stop();
                    return;
                }
            }
        }else{
            onError(ProgressStep.START, "Подключение к серверу по websocket не доступно.", null);
            stop();
        }
    }

    @Override
    protected Object sendBytes(final String tid, byte[] bytes) {
        if(bytes != null && getSocketManager() != null && getSocketManager().getSocket() != null) {
            onProgress(ProgressStep.UPLOAD, "", tid);
            UploadTransfer uploadTransfer = new UploadTransfer(this, getSocketManager().getSocket(), "v2", tid);
            transfers.put(tid, uploadTransfer);

            uploadTransfer.upload(bytes, new TransferStatusListeners() {
                @Override
                public void onStartTransfer(String tid, Transfer transfer) {
                    onProgressTransfer(TransferListeners.START, tid, transfer, null);
                }

                @Override
                public void onRestartTransfer(String tid, Transfer transfer) {
                    onProgressTransfer(TransferListeners.RESTART, tid, transfer, null);
                }

                @Override
                public void onPercentTransfer(String tid, TransferProgress progress, Transfer transfer) {
                    onProgressTransfer(TransferListeners.PERCENT, tid, transfer, progress);
                }

                @Override
                public void onStopTransfer(String tid, Transfer transfer) {
                    onProgressTransfer(TransferListeners.STOP, tid, transfer, null);
                }

                @Override
                public void onEndTransfer(String tid, Transfer transfer, Object data) {
                    onProgressTransfer(TransferListeners.END, tid, transfer, data);
                    getSocketManager().getSocket().emit("synchronization", tid, "v2");
                }

                @Override
                public void onErrorTransfer(String tid, String message, Transfer transfer) {
                    onProgressTransfer(TransferListeners.ERROR, tid, transfer, message);
                    onError(ProgressStep.UPLOAD, message, tid);
                }
            });
        }

        return null;
    }

    @Override
    protected Object sendBytes(final String tid, byte[] bytes, FileTransferFinishedCallback fileTransferFinishedCallback) {
        if(bytes != null && getSocketManager() != null && getSocketManager().getSocket() != null) {
            onProgress(ProgressStep.UPLOAD, "", tid);
            UploadTransfer uploadTransfer = new UploadTransfer(this, getSocketManager().getSocket(), "v2", tid);
            transfers.put(tid, uploadTransfer);

            uploadTransfer.upload(bytes, new TransferStatusListeners() {
                @Override
                public void onStartTransfer(String tid, Transfer transfer) {
                    onProgressTransfer(TransferListeners.START, tid, transfer, null);
                }

                @Override
                public void onRestartTransfer(String tid, Transfer transfer) {
                    onProgressTransfer(TransferListeners.RESTART, tid, transfer, null);
                }

                @Override
                public void onPercentTransfer(String tid, TransferProgress progress, Transfer transfer) {
                    onProgressTransfer(TransferListeners.PERCENT, tid, transfer, progress);
                }

                @Override
                public void onStopTransfer(String tid, Transfer transfer) {
                    onProgressTransfer(TransferListeners.STOP, tid, transfer, null);
                }

                @Override
                public void onEndTransfer(String tid, Transfer transfer, Object data) {
                    onProgressTransfer(TransferListeners.END, tid, transfer, data);
                    getSocketManager().getSocket().emit("synchronization", tid, "v2");
                    fileTransferFinishedCallback.onFileTransferFinish();
                }

                @Override
                public void onErrorTransfer(String tid, String message, Transfer transfer) {
                    onProgressTransfer(TransferListeners.ERROR, tid, transfer, message);
                    onError(ProgressStep.UPLOAD, message, tid);
                }
            });
        }

        return null;
    }

    @Override
    public void stop() {
        if(synchronizationListener != null && getSocketManager() != null && getSocketManager().getSocket() != null) {
            getSocketManager().getSocket().off("synchronization", synchronizationListener);
            getSocketManager().getSocket().off("synchronization-status", synchronizationListener);
        }

        if(synchronizationConnectListener != null && getSocketManager() != null && getSocketManager().getSocket() != null) {
            getSocketManager().getSocket().off(Socket.EVENT_DISCONNECT, synchronizationConnectListener);

            getSocketManager().getSocket().off(Socket.EVENT_CONNECT, synchronizationConnectListener);
            getSocketManager().getSocket().off(Socket.EVENT_CONNECT_ERROR, synchronizationConnectListener);
        }

        if(transfers != null) {
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
            switch (part) {
                case TransferListeners.START:
                    progressListener.onStartTransfer(tid, transfer);
                    break;

                case TransferListeners.RESTART:
                    progressListener.onRestartTransfer(tid, transfer);
                    break;

                case TransferListeners.PERCENT:
                    progressListener.onPercentTransfer(tid, (TransferProgress) data, transfer);
                    break;

                case TransferListeners.STOP:
                    progressListener.onStopTransfer(tid, transfer);
                    break;

                case TransferListeners.END:
                    progressListener.onEndTransfer(tid, transfer, data);
                    break;

                case TransferListeners.ERROR:
                    progressListener.onErrorTransfer(tid, (String) data, transfer);
                    break;
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
                }catch (Exception e) {
                    LogUtilSingleton.getInstance().writeText(e.toString());
                    onError(ProgressStep.UPLOAD, e, null);
                }
            //}
        }
        /**
         * обработка результат
         * @param args параметры
         */
        void processing(Object[] args) {
            if(args != null && args.length > 0) {
                Object item = args[0];
                if(item instanceof String){
                    onProgress(ProgressStep.NONE, (String)item, null);
                }else if(item instanceof SocketIOException){
                    onError(ProgressStep.NONE, StringUtil.exceptionToString((SocketIOException)item), null);
                } else if(item instanceof EngineIOException){
                    onError(ProgressStep.NONE, StringUtil.exceptionToString((EngineIOException)item), null);
                } else if(item instanceof Integer){
                    onProgress(ProgressStep.NONE, String.valueOf(item), null);
                }
            } else {
                onProgress(ProgressStep.NONE, "ERROR", null);
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
                    LogUtilSingleton.getInstance().writeText(e.toString());
                    //Logger.error(e);
                    onError(ProgressStep.UPLOAD, e, null);
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
                onError(ProgressStep.RESTORE, errorMsg, null);
                return;
            }

            JSONObject jsonObject = (JSONObject) args[0];
            try {
                // данный кусок кода нужен для вывода сообщений от сервера см. modules/synchronization/v1.js метод socketSend
                String tid = jsonObject.getString("tid");
                if(getSynchronization().getEntities(tid).length > 0) {
                    onProgress(ProgressStep.UPLOAD, jsonObject.getString("result"), tid);
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
                        DownloadTransfer downloadTransfer = new DownloadTransfer(getSynchronization(), getSocketManager().getSocket(), "v2", tid);
                        if(transfers.get(tid) != null) {
                            Objects.requireNonNull(transfers.get(tid)).destroy();
                        }
                        transfers.put(tid, downloadTransfer);

                        downloadTransfer.download(new TransferStatusListeners() {

                            @Override
                            public void onStartTransfer(String tid, Transfer transfer) {
                                onProgressTransfer(TransferListeners.START, tid, transfer, null);
                            }

                            @Override
                            public void onRestartTransfer(String tid, Transfer transfer) {
                                onProgressTransfer(TransferListeners.RESTART, tid, transfer, null);
                            }

                            @Override
                            public void onPercentTransfer(String tid, TransferProgress progress, Transfer transfer) {
                                onProgressTransfer(TransferListeners.PERCENT, tid, transfer, progress);
                            }

                            @Override
                            public void onStopTransfer(String tid, Transfer transfer) {
                                onProgressTransfer(TransferListeners.STOP, tid, transfer, null);
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
                                                    onProgressTransfer(TransferListeners.END, result.mTid, result.mTransfer, null);
                                                    if (isEntityFinished()) {
                                                        stop();
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
                                onProgressTransfer(TransferListeners.ERROR, tid, transfer, message);
                                onError(ProgressStep.DOWNLOAD, message, tid);
                            }
                        });
                    }
                }else{
                    // тут текст ошибки
                    onError(ProgressStep.RESTORE, jsonData.getString("msg"), null);
                    stop();
                }
            } catch (JSONException e) {
                onError(ProgressStep.RESTORE, e, null);
                stop();
            }
        }
    }
}
