package ru.mobnius.core.data;

import com.google.gson.annotations.Expose;

import java.io.IOException;

import ru.mobnius.core.data.rpc.RPCResult;
import ru.mobnius.core.data.rpc.SingleItemQuery;
import ru.mobnius.core.data.socket.SocketManager;

public class NotificationManager {
    private String mToken;
    private String mBaseUrl;

    public NotificationManager(String baseUrl, String token) {
        mToken = token;
        mBaseUrl = baseUrl;
    }

    /**
     * Количество новых уведомлений на сервере
     * @return кол-во уведомлений
     */
    public int getNewMessageCount() throws IOException {
        RPCResult[] rpcResults = RequestManager.rpc(mBaseUrl, mToken, "notification", "getUserNotifications", new SingleItemQuery());
        if(rpcResults != null && rpcResults.length > 0) {
            RPCResult rpcResult = rpcResults[0];
            if(rpcResult.isSuccess()) {
                if(rpcResult.result.records.length > 0) {
                    return rpcResult.result.records[0].get("n_count").getAsInt();
                }
            }
        }
        return -1;
    }

    /**
     * Изменение статуса уведомления о том, что о прочитан
     * @param ids иден. уведомлений.
     * @return true - запрос выполнен без ошибок
     */
    public boolean changeStatus(String[] ids) throws IOException {
        RPCResult[] rpcResults = RequestManager.rpc(mBaseUrl, mToken, "notification", "changeStatus", new SingleItemQuery(new Selection(ids)));
        if(rpcResults != null && rpcResults.length > 0) {
            RPCResult rpcResult = rpcResults[0];
            return rpcResult.isSuccess();
        }

        return false;
    }

    /**
     * Все уведомления доставлены и прочитаны
     * @return true - запрос выполнен без ошибок
     */
    public boolean changeStatusAll() throws IOException {
        RPCResult[] rpcResults = RequestManager.rpc(mBaseUrl, mToken, "notification", "changeStatusAll", new SingleItemQuery());
        if(rpcResults != null && rpcResults.length > 0) {
            RPCResult rpcResult = rpcResults[0];
            return rpcResult.isSuccess();
        }

        return false;
    }

    /**
     * Уведомления доставлены на мобильное устройство
     * @return true - запрос выполнен без ошибок
     */
    public boolean sended() throws IOException {
        RPCResult[] rpcResults = RequestManager.rpc(mBaseUrl, mToken, "notification", "sended", new SingleItemQuery());
        if(rpcResults != null && rpcResults.length > 0) {
            RPCResult rpcResult = rpcResults[0];
            return rpcResult.isSuccess();
        }

        return false;
    }

    /**
     * Отправка уведомления на сервер
     * @param body текст
     * @param to кому
     * @param group гпуппа
     */
    @SuppressWarnings("PrimitiveArrayArgumentToVarargsMethod")
    public void sendMessage(String body, int to, String group) {
        // отправляем текущее местоположение пользователя
        SocketManager socketManager = SocketManager.getInstance();
        if(socketManager != null && socketManager.isRegistered()) {
            //socketManager.getSocket().emit("notification", new StringMail(body, to, group).getBytes());
        }
    }

    private static class Selection {
        public Selection(String[] values) {
            selection = values;
        }

        @Expose
        public String[] selection;
    }
}
