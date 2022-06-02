package com.mobwal.android.library.data.rpc;

/**
 * метаописание результата запроса
 */
public class RPCResultMeta {
    /**
     * статус выполнения, если true - то выполнен удачно
     */
    public boolean success;

    /**
     * текст сообщения, если запрос завершился с ошибкой: success = false
     */
    public String msg;
}
