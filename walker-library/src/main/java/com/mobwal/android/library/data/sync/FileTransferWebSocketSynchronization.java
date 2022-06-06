package com.mobwal.android.library.data.sync;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mobwal.android.library.Constants;
import com.mobwal.android.library.data.sync.util.FullServerSidePackage;
import com.mobwal.android.library.data.sync.util.PackageResult;
import com.mobwal.android.library.data.sync.util.TableQuery;
import com.mobwal.android.library.socket.SocketManager;
import com.mobwal.android.library.sql.SQLContext;
import com.mobwal.android.library.util.StringUtil;

import java.io.IOException;

import com.mobwal.android.library.FileManager;
import com.mobwal.android.library.data.rpc.RPCItem;
import com.mobwal.android.library.data.rpc.RPCResult;
import com.mobwal.android.library.util.PackageCreateUtils;
import com.mobwal.android.library.util.PackageReadUtils;

/**
 * синхронизация по передачи данных по websocket с вложениями
 */
public abstract class FileTransferWebSocketSynchronization
        extends WebSocketSynchronization {

    /**
     * идентификтаор транзакций для вложений
     */
    public String fileTid;

    /**
     * применяются вложения или нет
     */
    protected boolean useAttachments = false;

    /**
     * Управление файловой системой
     */
    private FileManager fileManager;

    /**
     * конструктор
     *
     * @param name        имя
     * @param fileManager файловый менеджер
     */
    public FileTransferWebSocketSynchronization(SQLContext context, String name, FileManager fileManager, boolean zip) {
        super(context, name, zip);

        this.fileManager = fileManager;
    }

    /**
     * получение файлового менеджера
     *
     * @return возвращается объект
     */
    public FileManager getFileManager() {
        return fileManager;
    }

    @Override
    public void start(@NonNull SocketManager socketManager, @NonNull Activity activity, @NonNull ProgressListeners progress) {
        super.start(socketManager, activity, progress);
        onProgress(ProgressStep.START, "пакет file=" + StringUtil.getShortGuid(fileTid), fileTid);
    }

    @Override
    public byte[] generatePackage(String tid, Object... args) throws IOException {
        PackageCreateUtils utils = new PackageCreateUtils(isZip());
        for (Entity entity : getEntities()) {
            // обработка только элемента с указанным ключом
            if (entity.tid.equals(tid)) {
                if (tid.equals(fileTid) && useAttachments && entity instanceof EntityAttachment) {
                    // тут только обрабатывается добавление
                    Object[] records = getRecords(entity.tableName, tid).toArray();
                    FileManager manager = getFileManager();
                    if (records.length > 0) {

                        for (Object record : records) {
                            if (record instanceof OnFileListeners) {
                                OnFileListeners file = (OnFileListeners) record;
                                if (file.getIsDelete()) {
                                    continue;
                                }
                                try {
                                    byte[] bytes = manager.readPath(file.getFolder(), file.getC_name());
                                    if (bytes != null) {
                                        utils.addFile(file.getC_name(), file.getId(), bytes);
                                    } else {
                                        Log.e(Constants.TAG, "Найден файл длина которого равна null", new Exception());
                                    }
                                } catch (IOException e) {
                                    onError(ProgressStep.PACKAGE_CREATE, "При обработке файла " + file.getC_name() + " возникла ошибка", tid);
                                }
                            }

                            if (record instanceof OnAttachmentListeners) {
                                OnAttachmentListeners attachment = (OnAttachmentListeners) record;
                                if (attachment.isDelete()) {
                                    continue;
                                }
                                try {
                                    byte[] bytes = manager.readPath(FileManager.PHOTOS, attachment.getPath());
                                    if (bytes != null) {
                                        utils.addFile(attachment.getId(), attachment.getId(), bytes);
                                    }
                                } catch (IOException e) {
                                    onError(ProgressStep.PACKAGE_CREATE, "При обработке вложения " + attachment.getPath() + " возникла ошибка", tid);
                                }
                            }
                        }
                    }
                }

                if (entity.to) {
                    processingPackageTo(utils, entity.tableName, tid);
                }

                if (entity.from) {
                    TableQuery tableQuery = new TableQuery(entity.tableName, entity.change, entity.select);
                    RPCItem rpcItem;
                    if (entity.useCFunction) {
                        rpcItem = tableQuery.toRPCSelect(entity.params);
                        rpcItem.action = rpcItem.getFunctionName();
                    } else {
                        rpcItem = tableQuery.toRPCQuery(MAX_COUNT_IN_QUERY, entity.filters);
                    }
                    rpcItem.schema = entity.schema;
                    utils.addFrom(rpcItem);
                }
            }
        }
        return utils.generatePackage(tid);
    }

    @Override
    public void onProcessingPackage(PackageReadUtils utils, String tid) {
        /*
        Если хоть одна вставка была ошибочной, данные не добавлять
         */
        boolean success = true;
        try {
            for (RPCResult result : utils.getToResult()) { // при добавление информации была ошибка на сервере.
                Entity entity = getEntity(result.action);

                PackageResult packageResult = serverSidePackage.to(getContext(), result, tid, entity.clearable);
                if (!packageResult.success) {
                    onError(ProgressStep.RESTORE, packageResult.message, tid);
                }
                if (success && !packageResult.success) {
                    success = false;
                }
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, e.toString());
            onError(ProgressStep.RESTORE, e, tid);
            success = false;
        }

        if (!success) {
            return;
        }

        try {
            FullServerSidePackage fullServerSidePackage = (FullServerSidePackage) serverSidePackage;
            if (useAttachments) {
                fullServerSidePackage.attachmentBy(getFileManager());
            }
            fullServerSidePackage.setFileBinary(utils.getFiles());

            for (RPCResult result : utils.getFromResult()) {
                if (!result.meta.success) {
                    onError(ProgressStep.PACKAGE_CREATE, result.meta.msg, tid);
                    continue;
                }

                result.action = result.getNormalAction();
                String tableName = result.action;
                Entity entity = getEntity(tableName);
                PackageResult packageResult;
                if (tid.equals(fileTid) && useAttachments) {
                    packageResult = serverSidePackage.from(getContext(), result, tid, entity.to, true);
                } else {
                    packageResult = serverSidePackage.from(getContext(), result, tid, entity.to, false);
                }

                if (!packageResult.success) {
                    onError(ProgressStep.PACKAGE_CREATE, packageResult.message, tid);

                    if (packageResult.result instanceof Exception) {
                        Log.e(Constants.TAG, String.valueOf((Exception) packageResult.result));
                    }
                }
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, String.valueOf(e));
            onError(ProgressStep.PACKAGE_CREATE, e, tid);
        }
    }
}
