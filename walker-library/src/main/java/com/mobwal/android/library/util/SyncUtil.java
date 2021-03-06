package com.mobwal.android.library.util;

import android.database.sqlite.SQLiteDatabase;

import com.mobwal.android.library.FieldNames;
import com.mobwal.android.library.LogManager;
import com.mobwal.android.library.data.sync.Entity;
import com.mobwal.android.library.data.sync.ProgressStep;
import com.mobwal.android.library.data.sync.OnSynchronizationListeners;

public class SyncUtil {

    /**
     * объект для сброса параметров транзакции
     */
    public static Object[] getResetTidParams(){
        Object[] params = new Object[2];
        params[0] = null;
        params[1] = null;
        return params;
    }

    /**
     * запрос на сброса транзакции
     * @param tableName имя таблицы
     * @return sql-запрос
     */
    public static String getResetTidSqlQuery(String tableName) {
        return "update " + tableName + " set "+ FieldNames.TID +" = ?, "+ FieldNames.BLOCK_TID +" = ?";
    }

    /**
     * сбрасывает идентификаторы транзакций у таблиц по которым разрешено отправлять данные
     * @param context объект синхронизации
     * @return false - информация не была сброшена
     */
    public static boolean resetTid(OnSynchronizationListeners context) {
        boolean result;
        SQLiteDatabase db = context.getContext().getWritableDatabase();
        db.beginTransaction();
        try {
            Object[] params = getResetTidParams();
            for(Entity entity : context.getEntityToList()) {
                db.execSQL(getResetTidSqlQuery(entity.tableName), params);
            }
            db.setTransactionSuccessful();
            result = true;
        } catch (Exception e){
            result = false;
            LogManager.getInstance().info(e.toString());
        } finally {
            db.endTransaction();
        }

        return result;
    }

    /**
     * обновление идентификатора транзакции для записи
     * @param context объект синхронизации
     * @param tableName имя таблицы
     * @param tid идентификатор транзакции
     * @return возвращается результат обработки
     */
    public static boolean updateTid(OnSynchronizationListeners context, String tableName, String tid) {
        boolean result = false;
        try {
            Object[] params = new Object[2];
            params[0] = tid;
            params[1] = "";
            context.getContext().exec("update " + tableName + " set "+FieldNames.TID+" = ? where "+FieldNames.TID+" is null OR "+FieldNames.TID+" = ?", params);
            result = true;
        } catch (Exception e) {
            LogManager.getInstance().info(e.toString());
            context.onError(ProgressStep.START, e, tid);
        }
        return result;
    }

    /**
     * обновление идентификатора блока для записи
     * @param context объект синхронизации
     * @param tableName имя таблицы
     * @param tid идентификатор транзакции
     * @param blockTid идентификатор транзакции
     * @param linkName имя первичного ключа
     * @param linkValue значение первичного ключа
     * @return возвращается результат обработки
     */
    public static boolean updateBlockTid(OnSynchronizationListeners context, String tableName, String tid, String blockTid, String linkName, Object linkValue) {
        boolean result = false;
        //AbstractDaoSession daoSession = context.getDaoSession();
        ///Database db = daoSession.getDatabase();
        try {
            Object[] params = new Object[2];
            params[0] = blockTid;
            params[1] = linkValue;
            context.getContext().exec("update " + tableName + " set "+ FieldNames.BLOCK_TID + " = ? where " + linkName + " = ?", params);
            result = true;
        }catch (Exception e){
            LogManager.getInstance().info(e.toString());
            context.onError(ProgressStep.START, e, tid);
        }
        return result;
    }

    /**
     * обновление идентификатора блока для записи
     * @param context объект синхронизации
     * @param tableName имя таблицы
     * @param tid идентификатор транзакции
     * @param blockTid идентификатор транзакции
     * @param operationType тип операции
     * @return возвращается результат обработки
     */
    public static boolean updateBlockTid(OnSynchronizationListeners context, String tableName, String tid, String blockTid, String operationType) {
        boolean result = false;
        //AbstractDaoSession daoSession = context.getDaoSession();
        //Database db = daoSession.getDatabase();
        try {
            Object[] params = new Object[3];
            params[0] = blockTid;
            params[1] = tid;
            params[2] = operationType;
            context.getContext().exec("update " + tableName + " set "+ FieldNames.BLOCK_TID+" = ? where " + FieldNames.TID + " = ? AND " + FieldNames.OBJECT_OPERATION_TYPE + " = ?", params);
            result = true;
        }catch (Exception e){
            LogManager.getInstance().info(e.toString());
            context.onError(ProgressStep.START, e, tid);
        }
        return result;
    }

    /**
     * обновление идентификатора блока для записи
     * @param context объект синхронизации
     * @param tableName имя таблицы
     * @param tid идентификатор транзакции
     * @return возвращается результат обработки
     */
    public static boolean updateBlockTid(OnSynchronizationListeners context, String tableName, String tid) {
        boolean result = false;
        //AbstractDaoSession daoSession = context.getDaoSession();
        //Database db = daoSession.getDatabase();
        try {
            Object[] params = new Object[3];
            params[0] = null;
            params[1] = null;
            params[2] = tid;

            context.getContext().exec("update " + tableName + " set "+ FieldNames.BLOCK_TID+" = ?, " + FieldNames.OBJECT_OPERATION_TYPE + " = ? where " + FieldNames.TID + " = ? ", params);
            result = true;
        }catch (Exception e){
            LogManager.getInstance().info(e.toString());
            context.onError(ProgressStep.STOP, e, tid);
        }
        return result;
    }

    /**
     * обновление идентификатора транзакции во всех связанных таблицах
     * @param context объект синхронизации
     * @param tid идентификатор транзакции
     * @return возвращается результат обработки
     */
    public static boolean updateTid(OnSynchronizationListeners context, String tid){
        boolean result;
        SQLiteDatabase db = context.getContext().getWritableDatabase();
        db.beginTransaction();
        try {
            Object[] params = new Object[2];
            params[0] = tid;
            params[1] = "";
            for(Entity entity : context.getEntityToList()) {
                db.execSQL("update " + entity.tableName + " set "+FieldNames.TID+" = ? where "+FieldNames.TID+" is null OR "+FieldNames.TID+" = ?", params);
            }
            db.setTransactionSuccessful();
            result = true;
        }catch (Exception e){
            result = false;
            LogManager.getInstance().info(e.toString());
        }finally {
            db.endTransaction();
        }
        return result;
    }
}