package com.mobwal.android.library.data.sync;

import android.app.Activity;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import com.mobwal.android.library.FieldNames;
import com.mobwal.android.library.authorization.BasicAuthorizationSingleton;
import com.mobwal.android.library.data.DbOperationType;
import com.mobwal.android.library.data.packager.MetaSize;
import com.mobwal.android.library.data.rpc.RPCItem;

import com.mobwal.android.library.data.sync.util.ServerSidePackageListeners;
import com.mobwal.android.library.socket.SocketManager;
import com.mobwal.android.library.sql.SQLContext;
import com.mobwal.android.library.util.PackageCreateUtils;
import com.mobwal.android.library.util.PackageReadUtils;

import com.mobwal.android.library.util.SyncUtil;
import com.mobwal.android.library.util.VersionUtil;

/**
 * Базовый абстрактный класс синхронизации
 */
public abstract class BaseSynchronization implements OnSynchronizationListeners {

    /**
     * Максимальное количество получаемых данных для одной сущности
     */
    public static final int MAX_COUNT_IN_QUERY = 100000;

    /**
     * текущее активити, нужно для выполнения операции в потоке
     */
    private Activity activity;

    /**
     * имя синхронизации
     */
    private final String name;

    /**
     * статус при завершения синхронизации
     */
    private FinishStatus finishStatus = FinishStatus.NONE;

    /**
     * Список сущностей участвующих в синхронизации
     */
    private final ArrayList<Entity> entities;

    /**
     * Обработчик реузльтат синхронизации
     */
    protected ProgressListeners progressListener;

    /**
     * ОБработчик результата от сервера
     */
    protected ServerSidePackageListeners serverSidePackage;

    /**
     * обработка RPC по одной записи.
     * Устанавливается true если в результате запроса возникает ошибка и нужно определить какая запись в этом виновата.
     */
    public boolean oneOnlyMode = false;

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Запущен ли процесс синхронизации
     */
    protected boolean isRunning = false;

    private final boolean zip;

    private final SQLContext mContext;

    private SocketManager mSocketManager;

    public SQLContext getContext() {
        return mContext;
    }

    public boolean isZip() {
        return zip;
    }

    /**
     * конструктор
     *
     * @param context Контекст
     * @param name    имя
     * @param zip     сжатие данных при синхронизации
     */
    protected BaseSynchronization(SQLContext context, String name, boolean zip) {
        mContext = context;
        this.zip = zip;
        entities = new ArrayList<>();
        this.name = name;
    }

    /**
     * возвращается имя синхронизации
     *
     * @return имя
     */
    public String getName() {
        return name;
    }

    /**
     * идентификатор пользователя
     *
     * @return идентификатор пользователя
     */
    public long getUserID() {
        return BasicAuthorizationSingleton.getInstance().getUser().getUserId();
    }

    /**
     * Изменение статуса завершения синхронизации
     *
     * @param status статус завершения синхронизации
     */
    public void changeFinishStatus(FinishStatus status) {
        finishStatus = status;
    }

    /**
     * статус завершения синхронизации
     *
     * @return статус
     */
    public FinishStatus getFinishStatus() {
        return finishStatus;
    }

    /**
     * Устанавливаем текущее активити.
     * Предназначено для вывода процесса синхронизации в UI потоке
     *
     * @param activity текущее активити
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * получение активного экрана
     *
     * @return Activity
     */
    public Activity getActivity() {
        return this.activity;
    }

    /**
     * Запуск на выполение
     *
     * @param activity экран
     * @param progress результат выполнения
     */
    public void start(@NonNull SocketManager socketManager, @NonNull Activity activity, @NonNull ProgressListeners progress) {
        mSocketManager = socketManager;

        Log.e("SYNC", "BaseSynchronization start");
        if (isRunning) {
            this.stop();
        } else {
            isRunning = true;
        }
        initEntities();
        Log.e("SYNC", "BaseSynchronization initEntities");
        this.activity = activity;
        this.progressListener = progress;
        //resetTid(this);

        onProgress(ProgressStep.START, "", null);
        Log.e("SYNC", "BaseSynchronization onProgress");
        progressListener.onStart(this);
        Log.e("SYNC", "BaseSynchronization progressListener.onStart");
        changeFinishStatus(FinishStatus.NONE);
        Log.e("SYNC", "BaseSynchronization changeFinishStatus");
    }

    /**
     * Добавление сущности для обработки
     *
     * @param entity сущность
     */
    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    /**
     * Список сущностей
     *
     * @return возвращается список сущностей по которым разрешена отправка на сервер
     */
    public List<Entity> getEntityToList() {
        ArrayList<Entity> list = new ArrayList<>();
        for (Entity entity : entities) {
            if (entity.to) {
                list.add(entity);
            }
        }
        return list;
    }

    /**
     * Список сущностей которые обрабатываются
     *
     * @return список
     */
    public List<Entity> getEntities() {
        return Arrays.asList(entities.toArray(new Entity[0]));
    }

    /**
     * список записей которые подверглись изменению
     *
     * @param tableName имя таблицы
     * @param tid       иднтификатор транзакции
     * @return возвращается массив данных
     */
    public Collection getRecords(String tableName, String tid) {
        return getRecords(tableName, tid, null);
    }

    /**
     * список записей которые подверглись изменению
     *
     * @param tableName     имя таблицы
     * @param tid           иднтификатор транзакции
     * @param operationType тип операции
     * @return возвращается массив данных
     */
    protected Collection getRecords(String tableName, String tid, String operationType) {
        Class<?> objectClass = mContext.getClassFromName(tableName); // ReflectionUtil.getClassFromName(mContext.getContext(), tableName);

        if (tid.isEmpty()) {
            return mContext.select("select * from " + tableName, new String[0], objectClass);
        } else {
            if (operationType != null) {
                return mContext.select("select * from " + tableName + " where " + FieldNames.IS_SYNCHRONIZATION + " = 0 AND " + FieldNames.TID + " = ? AND " + FieldNames.OBJECT_OPERATION_TYPE + " = ?", new String[] { tid, operationType }, objectClass);
                //return queryBuilder.where(new WhereCondition.StringCondition(FieldNames.IS_SYNCHRONIZATION + " = 0 AND " + FieldNames.TID + " = ? AND " + FieldNames.OBJECT_OPERATION_TYPE + " = ?", tid, operationType)).list();
            } else {
                return mContext.select("select * from " + tableName + " where " + FieldNames.IS_SYNCHRONIZATION + " = 0 AND " + FieldNames.TID + " = ? AND " + FieldNames.OBJECT_OPERATION_TYPE + " is not null AND " + FieldNames.OBJECT_OPERATION_TYPE + " != ?", new String[] { tid, "" }, objectClass);
                //return queryBuilder.where(new WhereCondition.StringCondition(FieldNames.IS_SYNCHRONIZATION + " = 0 AND " + FieldNames.TID + " = ? AND " + FieldNames.OBJECT_OPERATION_TYPE + " is not null AND " + FieldNames.OBJECT_OPERATION_TYPE + " != ?", tid, "")).list();
            }
        }
        //return null;
    }

    /**
     * создание пакета с данными
     *
     * @param tid  идентификатор пакета
     * @param args дополнительная информация
     * @return возвращается массив байтов
     */
    protected abstract byte[] generatePackage(String tid, Object... args) throws Exception;

    /**
     * отправка данных на сервер
     *
     * @param tid   идентификатор транзакции
     * @param bytes массив байтов
     * @return результат отправки
     */
    protected abstract Object sendBytes(String tid, byte[] bytes);

    protected abstract Object sendBytes(String tid, byte[] bytes, FileTransferFinishedCallback fileTransferFinishedCallback);

    /**
     * инициализация сущностей
     */
    protected abstract void initEntities();

    /**
     * обработка пакета с результатом
     *
     * @param tid   список валидных идентификаторов пакета
     * @param bytes массив байтов
     */
    public void processingPackage(String[] tid, byte[] bytes) {
        onProgress(ProgressStep.PACKAGE_CREATE, "", null);
        PackageReadUtils utils = new PackageReadUtils(bytes, isZip());
        try {
            String currentTid = utils.getMeta().id;
            if (Arrays.asList(tid).contains(currentTid)) {
                updateFinishedByEntity(currentTid);
                if (utils.getMetaSize().status == MetaSize.CREATED) {
                    onProcessingPackage(utils, currentTid);
                } else {
                    onError(ProgressStep.PACKAGE_CREATE, "Статус пакета " + utils.getMetaSize().status + " не равен " + MetaSize.CREATED, currentTid);
                }
            }
        } catch (Exception e) {
            onError(ProgressStep.PACKAGE_CREATE, e, null);
        }

        utils.destroy();
    }

    /**
     * Обработчка пакета для отправки данных
     *
     * @param utils     объект для формирования пакета
     * @param tableName имя сущности
     * @param tid       идентификатор транзакции
     */
    protected void processingPackageTo(PackageCreateUtils utils, String tableName, String tid) {
        Object[] createRecords = getRecords(tableName, tid, DbOperationType.CREATED).toArray();
        Object[] updateRecords = getRecords(tableName, tid, DbOperationType.UPDATED).toArray();
        Object[] removeRecords = getRecords(tableName, tid, DbOperationType.REMOVED).toArray();

        String linkName = "id";

        if (createRecords.length > 0 || updateRecords.length > 0 || removeRecords.length > 0) {
            Entity entity = getEntity(tableName);
            if (oneOnlyMode && !entity.many) {
                if (createRecords.length > 0) {
                    for (Object o : createRecords) {
                        Object linkValue = getLinkValue(o);
                        if (linkValue == null){
                            continue;
                        }
                        RPCItem rpc = RPCItem.addItem(tableName, o);
                        rpc.schema = entity.schema;
                        utils.addTo(rpc);
                        SyncUtil.updateBlockTid(this, tableName, tid, String.valueOf(rpc.tid), linkName, linkValue);
                    }
                }

                if (updateRecords.length > 0) {
                    for (Object o : updateRecords) {
                        Object linkValue = getLinkValue(o);
                        if (linkValue == null){
                            continue;
                        }
                        RPCItem rpc = RPCItem.addItem(tableName, o);
                        rpc.schema = entity.schema;
                        utils.addTo(rpc);
                        SyncUtil.updateBlockTid(this, tableName, tid, String.valueOf(rpc.tid), linkName, linkValue);
                    }
                }

                if (removeRecords.length > 0) {
                    for (Object o : removeRecords) {
                        Object linkValue = getLinkValue(o);
                        if (linkValue == null){
                            continue;
                        }
                        RPCItem rpc = RPCItem.deleteItem(tableName, o);
                        rpc.schema = entity.schema;
                        utils.addTo(rpc);
                        SyncUtil.updateBlockTid(this, tableName, tid, String.valueOf(rpc.tid), linkName, linkValue);
                    }
                }
            } else {
                if (createRecords.length > 0) {
                    RPCItem rpc = RPCItem.addItems(tableName, createRecords);
                    rpc.schema = entity.schema;
                    utils.addTo(rpc);
                    SyncUtil.updateBlockTid(this, tableName, tid, String.valueOf(rpc.tid), DbOperationType.CREATED);
                }
                if (updateRecords.length > 0) {
                    RPCItem rpc = RPCItem.updateItems(tableName, updateRecords);
                    rpc.schema = entity.schema;
                    utils.addTo(rpc);
                    SyncUtil.updateBlockTid(this, tableName, tid, String.valueOf(rpc.tid), DbOperationType.UPDATED);
                }
                if (removeRecords.length > 0) {
                    RPCItem rpc = RPCItem.deleteItems(tableName, removeRecords);
                    rpc.schema = entity.schema;
                    utils.addTo(rpc);
                    SyncUtil.updateBlockTid(this, tableName, tid, String.valueOf(rpc.tid), DbOperationType.REMOVED);
                }
            }
        }
    }

    private Object getLinkValue(Object o) {
        try {
            Field fieldId = o.getClass().getDeclaredField("id");
            fieldId.setAccessible(true);
            return fieldId.get(o);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * обработкик пакетов
     *
     * @param utils объект для обработки
     * @param tid   идентификатор транзакции
     */
    protected abstract void onProcessingPackage(PackageReadUtils utils, String tid);

    /**
     * обработчик вывода статуса обработки
     *
     * @param step    шаг
     * @param message текст сообщения
     * @param tid     идентификатор транзакции
     */
    protected void onProgress(final int step, final String message, final String tid) {
        if (progressListener != null) {
            final OnSynchronizationListeners synchronization = this;
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (progressListener != null) {
                            progressListener.onProgress(synchronization, step, message, tid);
                        }
                    }
                });
            } else {
                progressListener.onProgress(this, step, message, tid);
            }
        }
    }

    /**
     * обработчик ошибок
     *
     * @param step шаг
     * @param e    исключение
     * @param tid  идентификатор транзакции
     */
    public void onError(int step, Exception e, String tid) {
        onError(step, e.toString(), tid);
    }

    /**
     * обработчик ошибок
     *
     * @param step    шаг
     * @param message текст сообщения
     * @param tid     идентификатор транзакции
     */
    public void onError(final int step, final String message, final String tid) {
        changeFinishStatus(FinishStatus.FAIL);
        if (progressListener != null) {
            final OnSynchronizationListeners synchronization = this;
            if (getActivity() != null) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressListener.onError(synchronization, step, message, tid);
                    }
                });
            } else {
                progressListener.onError(this, step, message, tid);
            }
        }
    }

    /**
     * Текущая сущность
     *
     * @param tableName имя таблицы
     * @return Возвращается текущая сущность
     */
    public Entity getEntity(String tableName) {
        Entity result = null;
        for (Entity entity : entities) {
            if (entity.tableName.equals(tableName)) {
                result = entity;
                break;
            }
        }
        return result;
    }

    /**
     * Возвращается список идентификатор пакетов
     *
     * @return массив идентификатор
     */
    protected String[] getCollectionTid() {
        String[] results = new String[entities.size()];
        int i = 0;
        for (Entity e : entities) {
            results[i] = e.tid;
            i++;
        }
        return results;
    }

    /**
     * Список сущностей
     *
     * @param tid идентификатор транзакции
     * @return возвращается список сущностей с tid
     */
    public Entity[] getEntities(String tid) {
        ArrayList<Entity> results = new ArrayList<>(entities.size());
        for (Entity entity : entities) {
            // обработка только элемента с указанным ключом
            if (entity.tid.equals(tid)) {
                results.add(entity);
            }
        }
        return results.toArray(new Entity[0]);
    }

    /**
     * Завершена ли обработка сущностей (пакетов)
     *
     * @return возвращается статус завершения
     */
    protected boolean isEntityFinished() {
        for (Entity entity : entities) {
            if (!entity.finished) {
                return false;
            }
        }
        return true;
    }

    /**
     * Обновление статуса finished у entity
     *
     * @param tid идентификатор пакета
     */
    protected void updateFinishedByEntity(String tid) {
        for (Entity entity : entities) {
            if (entity.tid.equals(tid)) {
                entity.setFinished();
            }
        }
    }

    /**
     * Принудительная остановка выполнения
     */
    public void stop() {
        if (FinishStatus.FAIL != finishStatus) {
            successStop();
        }
        onProgress(ProgressStep.STOP, "Синхронизация завершена.", null);

        SyncUtil.resetTid(this);
        entities.clear();
        if (activity != null) {
            activity = null;
        }
        progressListener = null;
        isRunning = false;
    }

    /**
     * Выполняется если была завершена удачно
     */
    public void successStop() {
        if (progressListener != null) {
            progressListener.onStop(this);
        }

        changeFinishStatus(FinishStatus.SUCCESS);
    }

    protected String getAppVersion() {
        return VersionUtil.getVersionName(mContext.getContext());
    }

    /**
     * удаление объекта
     */
    public void destroy() {
        stop();
        progressListener = null;
        if (activity != null) {
            activity = null;
        }
        changeFinishStatus(FinishStatus.NONE);
    }

    public SocketManager getSocketManager() {
        return mSocketManager;
    }

    protected interface FileTransferFinishedCallback {
        void onFileTransferFinish();
    }
}
