package com.mobwal.android.library.data.sync.util;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mobwal.android.library.Constants;
import com.mobwal.android.library.FieldNames;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.mobwal.android.library.SimpleFileManager;
import com.mobwal.android.library.data.packager.FileBinary;
import com.mobwal.android.library.data.rpc.RPCResult;
import com.mobwal.android.library.sql.SQLContext;

/**
 * абстрактный обработчик результат от сервера
 */
public abstract class ServerSidePackage implements ServerSidePackageListeners {
    protected boolean deleteRecordBeforeAppend = false;
    protected SimpleFileManager fileManager;
    protected FileBinary[] mFileBinary;

    /**
     * Устанавливает параметр удаление записей при добавлении информации в БД
     *
     * @param value значение
     */
    public void setDeleteRecordBeforeAppend(boolean value) {
        deleteRecordBeforeAppend = value;
    }

    /**
     * Удалять записи при добавлении информации в БД
     *
     * @return true - все записи из таблицы будут удалены
     */
    public boolean getDeleteRecordBeforeAppend() {
        return deleteRecordBeforeAppend;
    }

    /**
     * добавление информации для обработки вложений
     *
     * @param fileManager файловый менеджер
     */
    public void attachmentBy(SimpleFileManager fileManager) {
        this.fileManager = fileManager;
    }

    /**
     * Добавление бинарного блока
     *
     * @param fileBinary бинарный блок
     */
    public void setFileBinary(FileBinary[] fileBinary) {
        this.mFileBinary = fileBinary;
    }

    /**
     * обработка блока to
     * результат обработки информации переданнйо в блоке to
     *
     * @param session    сессия
     * @param rpcResult  результат RPC
     * @param packageTid идентификатор пакета
     * @param clearable  требуетяс очистка после успешной передачи
     * @return результат
     */
    public PackageResult to(SQLContext session, RPCResult rpcResult, String packageTid, Boolean clearable) {
        if (rpcResult.meta.success) {
            if (clearable) {
                // тут все хорошо, нужно удалить все записи c tid
                //Database db = session.getDatabase();
                Object[] params = new Object[1];
                params[0] = packageTid;

                session.exec("delete from " + rpcResult.action + " where " + FieldNames.TID +" = ?", params);
                return PackageResult.success(null);
            } else {
                return to(session, rpcResult, packageTid);
            }
        } else {
            return PackageResult.fail(rpcResult.meta.msg, null);
        }
    }

    /**
     * обработка блока to
     * результат обработки информации переданнйо в блоке to
     *
     * @param session    сессия
     * @param rpcResult  результат RPC
     * @param packageTid идентификатор пакета
     * @return результат
     */
    public PackageResult to(SQLContext session, RPCResult rpcResult, String packageTid) {
        // если все хорошо обновляем запись в локальной БД
        String sqlQuery = "";
        Object[] values;
        try {
            // TODO: тут нужно переделать
            if (rpcResult.meta.success) {
                sqlQuery = "UPDATE " + rpcResult.action + " set " + FieldNames.IS_SYNCHRONIZATION + " = ?, " + FieldNames.OBJECT_OPERATION_TYPE + " = ?, " + FieldNames.TID + " = ?, " + FieldNames.BLOCK_TID + " = ? where " + FieldNames.TID + " = ? and " + FieldNames.BLOCK_TID + " = ?;";
                values = new Object[6];
                values[0] = 1;
                values[1] = null;
                values[2] = null;
                values[3] = null;
                values[4] = packageTid;
                values[5] = String.valueOf(rpcResult.tid);

                session.exec(sqlQuery, values);

                return PackageResult.success(null);
            } else {
                sqlQuery = "UPDATE " + rpcResult.action + " set " + FieldNames.IS_SYNCHRONIZATION + " = ?, " + FieldNames.TID + " = ?, " + FieldNames.BLOCK_TID + " = ? where " + FieldNames.TID + " = ? and " + FieldNames.BLOCK_TID + " = ?;";
                values = new Object[5];
                values[0] = 0;
                values[1] = null;
                values[2] = null;
                values[3] = packageTid;
                values[4] = String.valueOf(rpcResult.tid);
                session.exec(sqlQuery, values);
                Log.e(Constants.TAG, String.valueOf(new Exception(rpcResult.meta.msg)));
                return PackageResult.fail("Ошибка обработки блока на сервере. " + rpcResult.meta.msg, null);
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, String.valueOf(e));
            Log.e(Constants.TAG, String.valueOf(new Exception(new Gson().toJson(rpcResult))));

            return PackageResult.fail("Ошибка обновление положительного результат в БД. Запрос: " + sqlQuery + ". " + new Gson().toJson(rpcResult), e);
        }
    }

    /**
     * обработка блока from
     * результат обработки информации переданной в блоке from
     *
     * @param session           сессия
     * @param rpcResult         результат RPC
     * @param packageTid        идентификатор пакета
     * @param isRequestToServer может ли объект делать запрос на сервер.
     * @return результат
     */
    public PackageResult from(SQLContext session, RPCResult rpcResult, String packageTid, boolean isRequestToServer) {
        return from(session, rpcResult, packageTid, isRequestToServer, false);
    }

    /**
     * обработка блока from
     * результат обработки информации переданной в блоке from
     *
     * @param session           сессия
     * @param rpcResult         результат RPC
     * @param packageTid        идентификатор пакета
     * @param isRequestToServer может ли объект делать запрос на сервер.
     * @param attachmentUse     применяется обработка вложений
     * @return результат
     */
    public PackageResult from(SQLContext session, RPCResult rpcResult, String packageTid, boolean isRequestToServer, boolean attachmentUse) {
        boolean attachmentProcessing = attachmentUse && fileManager != null;
        if (rpcResult.meta.success) {
            String tableName = rpcResult.action;
            // TODO: нужно заменить на более выстрый вариант
            Class<?> classObject = session.getClassFromName(tableName); // ReflectionUtil.getClassFromName(session.getContext(), tableName);

            if (classObject == null) {
                return PackageResult.fail("Имя сущности не найдено в локальной БД. " + tableName + ".", new NullPointerException("AbstractDao not found."));
            }

            PackageResult packageResult;

            if (!rpcResult.method.equals("Query") && !rpcResult.method.equals("Select")) {
                return PackageResult.fail("Метод результата " + tableName + " должен быть Query. Текущее значение " + rpcResult.method, null);
            }

            if (getDeleteRecordBeforeAppend() && rpcResult.code != RPCResult.PERMATENT) {
                session.exec("delete from " + tableName, new Object[0]);
                if (attachmentProcessing) {
                    fileManager.deleteFolder();
                }
            }

            // тут значит кэш
            if (rpcResult.code == RPCResult.PERMATENT) {
                Log.d("PERMATENT", tableName);
            }

            if (rpcResult.result.records.length > 0) {
                // тут нужно подумать а если массив будет очень большой, то вставка не произойдет
                JsonObject firstObject = rpcResult.result.records[0];
                if (!firstObject.has("__error")) {

                    //SqlStatementInsertFromJSONObject sqlInsert = new SqlStatementInsertFromJSONObject(firstObject, tableName, isRequestToServer, abstractDao);
                    try {
                        List<Object> objects = new ArrayList<>();
                        for (JsonObject object : rpcResult.result.records) {

                            Object objectResponse = new Gson().fromJson(object, classObject);
                            objects.add(objectResponse);
                        }

                        boolean result = session.insertMany(objects.toArray(new Object[0]));

                        if (result) {
                            return PackageResult.success(null);
                        } else {
                            return PackageResult.fail("Ошибка вставки записей в таблицу " + tableName + ". ", new Exception());
                        }
                    } catch (Exception e) {
                        packageResult = PackageResult.fail("Ошибка вставки записей в таблицу " + tableName + ".", e);
                    }
                } else {
                    String error = firstObject.get("__error").getAsString();
                    packageResult = PackageResult.fail("Ошибка вставки записей в таблицу " + tableName + ".", new Exception(error));
                }
                //db.endTransaction();
                return packageResult;
            }
            return PackageResult.success(null);
        } else {
            Log.e(Constants.TAG, String.valueOf(new Exception(rpcResult.meta.msg)));
            return PackageResult.fail("Ошибка обработки блока на сервере. " + rpcResult.meta.msg, null);
        }
        //return null;
    }

    /**
     * Получение файла по имени
     *
     * @param name имя
     * @return возарщается файл
     */
    protected FileBinary getFile(String name) {
        if (mFileBinary != null) {
            for (FileBinary file : mFileBinary) {
                if (file.name.equals(name)) {
                    return file;
                }
            }
        }
        return null;
    }
}
