package com.mobwal.android.library.socket;

public interface OnSocketListeners {
    /**
     * Соединение с сервером установлено
     */
    void onConnect();

    /**
     * Регистрация на сервере произведена
     */
    void onRegistry();

    /**
     * Отключение от сервера
     */
    void onDisconnect();
}
