package ru.mobnius.core.data.network;

public interface OnNetworkChangeListeners {

    /**
     * Обработчик изменения сети
     * @param isOnline приложение в онлайн
     * @param isSocketConnect сокет соединение
     * @param isServerExists подключение к серверу доступно.
     */
    // TODO 28/01/2020 доступность подключения к серверу не определяется автоматически только при измеении подключения к интернету
    void onNetworkChange(boolean isOnline, boolean isSocketConnect, boolean isServerExists);
}
