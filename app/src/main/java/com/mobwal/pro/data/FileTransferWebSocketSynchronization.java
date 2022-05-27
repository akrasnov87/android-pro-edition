package com.mobwal.pro.data;

import android.app.Activity;

import com.mobwal.pro.WalkerSQLContext;

import java.io.IOException;

import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.data.rpc.RPCItem;
import ru.mobnius.core.data.rpc.RPCResult;
//import com.mobwal.pro.data.meta.TableQuery;
//import com.mobwal.pro.data.utils.FullServerSidePackage;
//import com.mobwal.pro.data.utils.PackageResult;
import ru.mobnius.core.utils.PackageCreateUtils;
import ru.mobnius.core.utils.PackageReadUtils;
import ru.mobnius.core.utils.StringUtil;

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
     * @param session     сессия для подключения к БД
     * @param name        имя
     * @param fileManager файловый менеджер
     */
    public FileTransferWebSocketSynchronization(WalkerSQLContext context, String name, FileManager fileManager, boolean zip) {
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
    public void start(Activity activity, IProgress progress) {
        super.start(activity, progress);
        onProgress(IProgressStep.START, "пакет file=" + StringUtil.getShortGuid(fileTid), fileTid);
    }

    @Override
    public byte[] generatePackage(String tid, Object... args) throws IOException {
        PackageCreateUtils utils = new PackageCreateUtils(isZip());
        for (Entity entity : getEntities()) {
            // обработка только элемента с указанным ключом
            if (entity.tid.equals(tid)) {
                if (tid.equals(fileTid) && useAttachments) {
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
                                        Logger.error("Найден файл длина которого равна null", new Exception());
                                    }
                                } catch (IOException e) {
                                    onError(IProgressStep.PACKAGE_CREATE, "При обработке файла " + file.getC_name() + " возникла ошибка", tid);
                                }
                            }
                                /*
                            if (record instanceof OnAttachmentListeners) {
                                OnAttachmentListeners attachment = (OnAttachmentListeners) record;
                                if (attachment.getIsDelete()) {
                                    continue;
                                }
                                try {
                                    byte[] bytes = manager.readPath(FileManager.PHOTOS, attachment.getC_name());
                                    if(bytes != null) {
                                        utils.addFile(attachment.getId(), attachment.getId(), bytes);
                                    }
                                } catch (IOException e) {
                                    onError(IProgressStep.PACKAGE_CREATE, "При обработке вложения " + attachment.getC_name() + " возникла ошибка", tid);
                                }
                                  */
                        }
                    }
                }
                if (entity.to) {
                    processingPackageTo(utils, entity.tableName, tid);
                }
                if (entity.from) {
                    /*TableQuery tableQuery = new TableQuery(entity.tableName, entity.change, entity.select);
                    RPCItem rpcItem;
                    if (entity.useCFunction) {
                        rpcItem = tableQuery.toRPCSelect(entity.params);
                    } else {
                        rpcItem = tableQuery.toRPCQuery(MAX_COUNT_IN_QUERY, entity.filters);
                    }
                    utils.addFrom(rpcItem);*/
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

                /*PackageResult packageResult = serverSidePackage.to(getDaoSession(), result, tid, entity.clearable);
                if (!packageResult.success) {
                    onError(IProgressStep.RESTORE, packageResult.message, tid);
                }
                if (success && !packageResult.success) {
                    success = false;
                }*/
            }
        } catch (Exception e) {
            Logger.error(e);
            onError(IProgressStep.RESTORE, e, tid);
            success = false;
        }

        if (!success) {
            return;
        }

        try {
            /*FullServerSidePackage fullServerSidePackage = (FullServerSidePackage) serverSidePackage;
            if (useAttachments) {
                fullServerSidePackage.attachmentBy(getFileManager());
            }
            fullServerSidePackage.setFileBinary(utils.getFiles());

            for (RPCResult result : utils.getFromResult()) {
                if (!result.meta.success) {
                    onError(IProgressStep.PACKAGE_CREATE, result.meta.msg, tid);
                    continue;
                }
                String tableName = result.action;
                Entity entity = getEntity(tableName);
                PackageResult packageResult;
                if (tid.equals(fileTid) && useAttachments) {
                    packageResult = serverSidePackage.from(getDaoSession(), result, tid, entity.to, true);
                } else {
                    packageResult = serverSidePackage.from(getDaoSession(), result, tid, entity.to, false);
                }

                if (!packageResult.success) {
                    onError(IProgressStep.PACKAGE_CREATE, packageResult.message, tid);

                    if (packageResult.result instanceof Exception) {
                        Logger.error((Exception) packageResult.result);
                    }
                }
            }*/
        } catch (Exception e) {
            Logger.error(e);
            onError(IProgressStep.PACKAGE_CREATE, e, tid);
        }
    }
}
