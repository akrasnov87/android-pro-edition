package com.mobwal.android.library.data.sync.util;

import com.mobwal.android.library.FieldNames;
import com.mobwal.android.library.sql.SQLContext;

import com.mobwal.android.library.data.rpc.RPCResult;

/**
 * Обработчик для пакетов только в направлении сервера
 */
public class ToServerOnly extends ServerSidePackage {
    @Override
    public PackageResult to(SQLContext session, RPCResult rpcResult, String packageTid) {
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
