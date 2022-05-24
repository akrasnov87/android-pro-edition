package ru.mobnius.core.data.socket;

public interface OnSocketListeners {
    /**
     * Обработчик получения сообщения
     * @param type тип сообщения
     * @param buffer сообщение
     */
    void onPushMessage(String type, byte[] buffer);

    /**
     * Обработчик. Сообщение доставлено
     * @param buffer сообщение
     */
    void onPushDelivered(byte[] buffer);

    /**
     * Обработчик. Сообщение не доставлено
     * @param buffer сообщение
     */
    void onPushUnDelivered(byte[] buffer);

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
