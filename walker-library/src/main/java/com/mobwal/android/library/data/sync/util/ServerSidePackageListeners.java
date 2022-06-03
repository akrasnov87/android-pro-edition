package com.mobwal.android.library.data.sync.util;

//import org.greenrobot.greendao.AbstractDaoSession;
import com.mobwal.android.library.sql.SQLContext;

import org.json.JSONException;

import com.mobwal.android.library.data.rpc.RPCResult;


/**
 * интерфейс для обработки пакетов принятых от сервера
 */
public interface ServerSidePackageListeners {
    /**
     * обработка блока to
     * результат обработки информации переданнйо в блоке to
     * @param context сессия
     * @param rpcResult результат RPC
     * @param packageTid идентификатор пакета
     * @return результат
     */
    PackageResult to(SQLContext context, RPCResult rpcResult, String packageTid);

    /**
     * обработка блока to
     * результат обработки информации переданнйо в блоке to
     * @param context сессия
     * @param rpcResult результат RPC
     * @param packageTid идентификатор пакета
     * @param clearable требуетяс очистка после успешной передачи
     * @return результат
     */
    PackageResult to(SQLContext context, RPCResult rpcResult, String packageTid, Boolean clearable);

    /**
     * обработка блока from
     * результат обработки информации переданной в блоке from
     * @param context сессия
     * @param rpcResult результат RPC
     * @param packageTid идентификатор пакета
     * @param isRequestToServer может ли объект делать запрос на сервер.
     * @param attachmentUse применяется обработка вложений
     * @return результат
     */
    PackageResult from(SQLContext context, RPCResult rpcResult, String packageTid, boolean isRequestToServer, boolean attachmentUse) throws JSONException;
}
