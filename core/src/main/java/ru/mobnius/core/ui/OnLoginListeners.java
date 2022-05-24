package ru.mobnius.core.ui;

public interface OnLoginListeners {
    /**
     * Авторизация выполнена
     */
    void onAuthorized();

    /**
     * Требуется выполнить авторизацию
     */
    void onAuthorize();
}
