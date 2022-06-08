package com.mobwal.android.library.socket;

import android.util.Log;

import com.mobwal.android.library.Constants;
import com.mobwal.android.library.authorization.credential.BasicCredential;
import com.mobwal.android.library.data.packager.MetaSize;
import com.mobwal.android.library.data.packager.PackageUtil;
import com.mobwal.android.library.util.UrlUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Создание websocket подключения к серверу
 * Подробнее читать тут
 *
 * https://socket.io/blog/native-socket-io-and-android/
 * https://github.com/socketio/socket.io-client-java
 */
public class SocketManager {
    /**
     * Имя события регистрации на сервере
     */
    public final static String EVENT_REGISTRY = "registry";

    /**
     * Событие не авторизации
     */
    public final static String EVENT_NOT_AUTH = "not_auth";

    private Socket socket;
    private boolean isRegistry;

    private OnSocketListeners mListeners;

    /**
     * Подключение к сокету
     * @param url адресная строка подключения
     * @param credentials безопасность
     * @param imei IMEI
     */
    public SocketManager(String url, BasicCredential credentials, String imei) {
        /*
          Поддерживаемые протоколы транспорта
         */
        String[] transports = new String[1];
        transports[0] = "websocket";

        try {
            IO.Options opts = new IO.Options();
            opts.forceNew = true;
            opts.path = UrlUtil.getPathUrl(url) + "/socket.io";
            if(credentials != null || imei != null) {
                String query = "";
                if(credentials != null) {
                    query = "token=" + credentials.getToken();
                }

                if(imei != null){
                    query += (query.isEmpty() ? "" : "&") + "imei=" + imei;
                }
                opts.query = query;
            }
            opts.transports = transports;

            socket = IO.socket(UrlUtil.getDomainUrl(url), opts);

        } catch (URISyntaxException e) {
            Log.d(Constants.TAG, e.toString());
        }
    }

    /**
     * Открытие подключения к серверу
     * @param listeners обработчик уведомлений
     */
    public void open(final OnSocketListeners listeners) {
        mListeners = listeners;

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                listeners.onConnect();
            }
        });

        socket.on(EVENT_REGISTRY, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                isRegistry = true;
                listeners.onRegistry();
            }
        });

        socket.on(EVENT_NOT_AUTH, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject jsonObject = (JSONObject) args[0];
                try {
                    Log.d(Constants.TAG, jsonObject.getJSONObject("data").getString("msg"));
                } catch (JSONException ignored) {

                }
            }
        });

        socket.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                isRegistry = false;
                mListeners.onDisconnect();
            }
        });

        socket.connect();
    }

    /**
     * текущее сокет подключение
     * @return сокет соединение
     */
    public Socket getSocket() {
        return socket;
    }

    /**
     * зарегистрирован ли пользователь на сервере
     * @return true - пользователь был зарегистрирован ранее
     */
    public boolean isRegistered() {
        if(socket!= null)
            return isRegistry && socket.connected();
        return false;
    }

    /**
     * Подключение к сокет серверу доступно
     * @return true - подключение доступно
     */
    public boolean isConnected() {
        if(socket!= null)
            return socket.connected();
        return false;
    }

    /**
     * Закрытие подключения
     */
    public void close() {
        if(socket != null) {
            socket.off();
            socket.disconnect();
            socket.close();
        }

        isRegistry = false;

        if(mListeners != null) {
            mListeners.onDisconnect();
        }
    }

    /**
     * Удаление объекта
     */
    public void destroy() {
        close();

        mListeners = null;
        socket = null;
    }
}
