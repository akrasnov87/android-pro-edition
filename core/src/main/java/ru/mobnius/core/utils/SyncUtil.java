package ru.mobnius.core.utils;

import org.greenrobot.greendao.AbstractDaoSession;
import org.greenrobot.greendao.database.Database;

import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.data.storage.FieldNames;
/*import ru.mobnius.core.data.synchronization.Entity;
import ru.mobnius.core.data.synchronization.IProgressStep;
import ru.mobnius.core.data.synchronization.OnSynchronizationListeners;*/


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
    /*public static boolean resetTid(OnSynchronizationListeners context) {
        boolean result;
        AbstractDaoSession daoSession = context.getDaoSession();
        Database db = daoSession.getDatabase();
        db.beginTransaction();
        try {
            Object[] params = getResetTidParams();
            for(Entity entity : context.getEntityToList()) {
                db.execSQL(getResetTidSqlQuery(entity.tableName), params);
            }
            db.setTransactionSuccessful();
            result = true;
        }catch (Exception e){
            result = false;
            Logger.error(e);
            context.onError(IProgressStep.NONE, e, null);
        }finally {
            db.endTransaction();
        }

        return result;
    }*/

    /**
     * обновление идентификатора транзакции для записи
     * @param context объект синхронизации
     * @param tableName имя таблицы
     * @param tid идентификатор транзакции
     * @return возвращается результат обработки
     */
    /*public static boolean updateTid(OnSynchronizationListeners context, String tableName, String tid) {
        boolean result = false;
        AbstractDaoSession daoSession = context.getDaoSession();
        Database db = daoSession.getDatabase();
        try {
            Object[] params = new Object[2];
            params[0] = tid;
            params[1] = "";
            db.execSQL("update " + tableName + " set "+FieldNames.TID+" = ? where "+FieldNames.TID+" is null OR "+FieldNames.TID+" = ?", params);
            result = true;
        } catch (Exception e) {
            Logger.error(e);
            context.onError(IProgressStep.START, e, tid);
        }
        return result;
    }*/

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
    /*public static boolean updateBlockTid(OnSynchronizationListeners context, String tableName, String tid, String blockTid, String linkName, Object linkValue) {
        boolean result = false;
        AbstractDaoSession daoSession = context.getDaoSession();
        Database db = daoSession.getDatabase();
        try {
            Object[] params = new Object[2];
            params[0] = blockTid;
            params[1] = linkValue;
            db.execSQL("update " + tableName + " set "+ FieldNames.BLOCK_TID + " = ? where " + linkName + " = ?", params);
            result = true;
        }catch (Exception e){
            Logger.error(e);
            context.onError(IProgressStep.START, e, tid);
        }
        return result;
    }*/

    /**
     * обновление идентификатора блока для записи
     * @param context объект синхронизации
     * @param tableName имя таблицы
     * @param tid идентификатор транзакции
     * @param blockTid идентификатор транзакции
     * @param operationType тип операции
     * @return возвращается результат обработки
     */
    /*public static boolean updateBlockTid(OnSynchronizationListeners context, String tableName, String tid, String blockTid, String operationType) {
        boolean result = false;
        AbstractDaoSession daoSession = context.getDaoSession();
        Database db = daoSession.getDatabase();
        try {
            Object[] params = new Object[3];
            params[0] = blockTid;
            params[1] = tid;
            params[2] = operationType;
            db.execSQL("update " + tableName + " set "+ FieldNames.BLOCK_TID+" = ? where " + FieldNames.TID + " = ? AND " + FieldNames.OBJECT_OPERATION_TYPE + " = ?", params);
            result = true;
        }catch (Exception e){
            Logger.error(e);
            context.onError(IProgressStep.START, e, tid);
        }
        return result;
    }*/

    /**
     * обновление идентификатора блока для записи
     * @param context объект синхронизации
     * @param tableName имя таблицы
     * @param tid идентификатор транзакции
     * @return возвращается результат обработки
     */
    /*public static boolean updateBlockTid(OnSynchronizationListeners context, String tableName, String tid) {
        boolean result = false;
        AbstractDaoSession daoSession = context.getDaoSession();
        Database db = daoSession.getDatabase();
        try {
            Object[] params = new Object[3];
            params[0] = null;
            params[1] = null;
            params[2] = tid;

            db.execSQL("update " + tableName + " set "+ FieldNames.BLOCK_TID+" = ?, " + FieldNames.OBJECT_OPERATION_TYPE + " = ? where " + FieldNames.TID + " = ? ", params);
            result = true;
        }catch (Exception e){
            Logger.error(e);
            context.onError(IProgressStep.STOP, e, tid);
        }
        return result;
    }*/

    /**
     * обновление идентификатора транзакции во всех связанных таблицах
     * @param context объект синхронизации
     * @param tid идентификатор транзакции
     * @return возвращается результат обработки
     */
    /*public static boolean updateTid(OnSynchronizationListeners context, String tid){
        boolean result;
        AbstractDaoSession daoSession = context.getDaoSession();
        Database db = daoSession.getDatabase();
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
            Logger.error(e);
        }finally {
            db.endTransaction();
        }
        return result;
    }*/
}