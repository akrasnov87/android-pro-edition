package ru.mobnius.core.data;

/**
 * Универсальный обработчик функций обратного вызова
 */
public interface OnCallbackListener {
    /**
     * результат обработки обратного вызова
     * @param meta результат
     */
    void onResult(Meta meta);
}
