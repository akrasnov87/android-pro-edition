package ru.mobnius.core.data.synchronization;

/**
 * Статус завершения синхронизации
 */
public enum FinishStatus {
    /**
     * неизвестно
     */
    NONE,
    /**
     * Успешно
     */
    SUCCESS,
    /**
     * Завершено с ошибкой
     */
    FAIL,

    START
}
