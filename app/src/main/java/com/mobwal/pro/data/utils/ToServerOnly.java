package com.mobwal.pro.data.utils;

//import org.greenrobot.greendao.AbstractDaoSession;
//import org.greenrobot.greendao.database.Database;

import com.mobwal.pro.WalkerSQLContext;

import com.mobwal.android.library.data.rpc.RPCResult;
import ru.mobnius.core.data.storage.FieldNames;

/**
 * Обработчик для пакетов только в направлении сервера
 */
public class ToServerOnly extends ServerSidePackage {
    @Override
    public PackageResult to(WalkerSQLContext session, RPCResult rpcResult, String packageTid) {
        if(rpcResult.meta.success) {
            // тут все хорошо, нужно удалить все записи c tid
            Object[] params = new Object[1];
            params[0] = packageTid;

            session.exec("delete from " + rpcResult.action + " where " + FieldNames.TID + " = ?", params);
            return PackageResult.success(null);
        } else {
            return PackageResult.fail(rpcResult.meta.msg, null);
        }
    }
}
