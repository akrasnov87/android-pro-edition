package ru.mobnius.core.data.synchronization.utils;

import android.database.sqlite.SQLiteConstraintException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;

import java.io.FileNotFoundException;
import java.util.Objects;

import ru.mobnius.core.Names;
import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.data.packager.FileBinary;
import ru.mobnius.core.data.rpc.RPCResult;
import ru.mobnius.core.data.storage.FieldNames;
import ru.mobnius.core.utils.SqlStatementInsertFromJSONObject;
import ru.mobnius.core.utils.SqlUpdateFromJSONObject;
import ru.mobnius.core.utils.StringUtil;

/**
 * абстрактный обработчик результат от сервера
 */
public abstract class ServerSidePackage implements IServerSidePackage {
    protected boolean deleteRecordBeforeAppend = false;
    protected FileManager fileManager;
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
    public void attachmentBy(FileManager fileManager) {
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
    public PackageResult to(AbstractDaoSession session, RPCResult rpcResult, String packageTid, Boolean clearable) {
        if (rpcResult.meta.success) {
            if (clearable) {
                // тут все хорошо, нужно удалить все записи c tid
                Database db = session.getDatabase();
                Object[] params = new Object[1];
                params[0] = packageTid;

                db.execSQL("delete from " + rpcResult.action + " where tid = ?", params);
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
    public PackageResult to(AbstractDaoSession session, RPCResult rpcResult, String packageTid) {
        Database db = session.getDatabase();
        // если все хорошо обновляем запись в локальной БД
        String sqlQuery = "";
        Object[] values;
        try {
            // TODO: тут нужно переделать
            if (!rpcResult.meta.success) {
                sqlQuery = "UPDATE " + rpcResult.action + " set " + FieldNames.IS_SYNCHRONIZATION + " = ?, " + FieldNames.TID + " = ?, " + FieldNames.BLOCK_TID + " = ? where " + FieldNames.TID + " = ? and " + FieldNames.BLOCK_TID + " = ?;";
                values = new Object[5];
                values[0] = false;
                values[1] = null;
                values[2] = null;
                values[3] = packageTid;
                values[4] = String.valueOf(rpcResult.tid);

                db.execSQL(sqlQuery, values);
                Logger.error(new Exception(rpcResult.meta.msg));
                return PackageResult.fail("Ошибка обработки блока на сервере. " + rpcResult.meta.msg, null);
            } else {
                sqlQuery = "UPDATE " + rpcResult.action + " set " + FieldNames.IS_SYNCHRONIZATION + " = ?, " + FieldNames.OBJECT_OPERATION_TYPE + " = ?, " + FieldNames.TID + " = ?, " + FieldNames.BLOCK_TID + " = ? where " + FieldNames.TID + " = ? and " + FieldNames.BLOCK_TID + " = ?;";
                values = new Object[6];
                values[0] = true;
                values[1] = null;
                values[2] = null;
                values[3] = null;
                values[4] = packageTid;
                values[5] = String.valueOf(rpcResult.tid);

                db.execSQL(sqlQuery, values);

                return PackageResult.success(null);
            }
        } catch (Exception e) {
            Logger.error(e);
            Logger.error(new Exception(new Gson().toJson(rpcResult)));

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
    public PackageResult from(AbstractDaoSession session, RPCResult rpcResult, String packageTid, boolean isRequestToServer) {
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
    public PackageResult from(AbstractDaoSession session, RPCResult rpcResult, String packageTid, boolean isRequestToServer, boolean attachmentUse) {
        boolean attachmentProcessing = attachmentUse && fileManager != null;
        if (rpcResult.meta.success) {
            Database db = session.getDatabase();
            AbstractDao abstractDao = null;
            String tableName = rpcResult.action;
            for (AbstractDao ad : session.getAllDaos()) {
                if (ad.getTablename().equals(tableName)) {
                    abstractDao = ad;
                    break;
                }
            }

            if (abstractDao == null) {
                return PackageResult.fail("Имя сущности не найдено в локальной БД. " + tableName + ".", new NullPointerException("AbstractDao not found."));
            }

            PackageResult packageResult;

            if (!rpcResult.method.equals("Query") && !rpcResult.method.equals("Select")) {
                return PackageResult.fail("Метод результата " + tableName + " должен быть Query. Текущее значение " + rpcResult.method, null);
            }

            db.beginTransaction();

            if (getDeleteRecordBeforeAppend() && rpcResult.code != RPCResult.PERMATENT) {
                db.execSQL("delete from " + tableName);
                // таким образом очищаем кэш http://greenrobot.org/greendao/documentation/sessions/
                abstractDao.detachAll();
                if (attachmentProcessing) {
                    try {
                        fileManager.deleteFolder(tableName);
                        fileManager.deleteFolder(FileManager.PHOTOS);
                    } catch (FileNotFoundException ignored) {

                    }
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
                    SqlStatementInsertFromJSONObject sqlInsert = new SqlStatementInsertFromJSONObject(firstObject, tableName, isRequestToServer, abstractDao);
                    try {
                        for (JsonObject object : rpcResult.result.records) {
                            try {
                                if (attachmentProcessing) {
                                    String fileName = object.get(FieldNames.C_NAME).getAsString();
                                    FileBinary file = getFile(fileName);
                                    if (file != null && StringUtil.equalsIgnoreCase(tableName, FileManager.ATTACHMENTS)) {
                                        fileManager.writeBytes(tableName, file.name.replace(Names.VIDEO_EXTENSION, "." + PreferencesManager.getInstance().getImageFormat()), file.bytes);
                                    } else {
                                        // Logger.error(new Exception("Включен механизм обработки вложений. В результате ответа не найден файл с именем " + fileName + "."));
                                    }
                                }
                                sqlInsert.bind(object);
                            } catch (SQLiteConstraintException e) {
                                Log.e("SYNC_ERROR", Objects.requireNonNull(e.getMessage()));

                                // тут нужно обновить запись
                                String pkColumnName = "";
                                for (AbstractDao a : session.getAllDaos()) {
                                    if (a.getTablename().equals(tableName)) {
                                        pkColumnName = a.getPkProperty().columnName;
                                        break;
                                    }
                                }
                                if (pkColumnName.isEmpty()) {
                                    throw new Exception("Колонка для первичного ключа, таблицы " + tableName + " не найден.");
                                } else {
                                    // тут обновление будет только у тех записей у которых не было изменений.
                                    SqlUpdateFromJSONObject sqlUpdate = new SqlUpdateFromJSONObject(firstObject, tableName, pkColumnName, abstractDao);
                                    db.execSQL(sqlUpdate.convertToQuery(isRequestToServer), sqlUpdate.getValues(object, isRequestToServer));
                                }
                            }
                        }
                        db.setTransactionSuccessful();
                        packageResult = PackageResult.success(null);
                    } catch (Exception e) {
                        packageResult = PackageResult.fail("Ошибка вставки записей в таблицу " + tableName + ".", e);
                    }
                } else {
                    String error = firstObject.get("__error").getAsString();
                    packageResult = PackageResult.fail("Ошибка вставки записей в таблицу " + tableName + ".", new Exception(error));
                }
                db.endTransaction();
                return packageResult;
            } else {
                db.setTransactionSuccessful();
                db.endTransaction();
            }
            return PackageResult.success(null);
        } else {
            Logger.error(new Exception(rpcResult.meta.msg));
            return PackageResult.fail("Ошибка обработки блока на сервере. " + rpcResult.meta.msg, null);
        }
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
