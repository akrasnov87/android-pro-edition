package com.mobwal.pro.data.utils;

//import org.greenrobot.greendao.AbstractDaoSession;
import com.mobwal.pro.WalkerSQLContext;

import org.json.JSONException;

import ru.mobnius.core.data.rpc.RPCResult;


/**
 * интерфейс для обработки пакетов принятых от сервера
 */
public interface IServerSidePackage {
    /**
     * обработка блока to
     * результат обработки информации переданнйо в блоке to
     * @param session сессия
     * @param rpcResult результат RPC
     * @param packageTid идентификатор пакета
     * @return результат
     */
    PackageResult to(WalkerSQLContext context, RPCResult rpcResult, String packageTid);

    /**
     * обработка блока to
     * результат обработки информации переданнйо в блоке to
     * @param session сессия
     * @param rpcResult результат RPC
     * @param packageTid идентификатор пакета
     * @param clearable требуетяс очистка после успешной передачи
     * @return результат
     */
    PackageResult to(WalkerSQLContext context, RPCResult rpcResult, String packageTid, Boolean clearable);

    /**
     * обработка блока from
     * результат обработки информации переданной в блоке from
     * @param session сессия
     * @param rpcResult результат RPC
     * @param packageTid идентификатор пакета
     * @param isRequestToServer может ли объект делать запрос на сервер.
     * @param attachmentUse применяется обработка вложений
     * @return результат
     */
    PackageResult from(WalkerSQLContext context, RPCResult rpcResult, String packageTid, boolean isRequestToServer, boolean attachmentUse) throws JSONException;
}
