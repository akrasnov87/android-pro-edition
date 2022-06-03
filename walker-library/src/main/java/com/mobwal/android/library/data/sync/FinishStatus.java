package com.mobwal.android.library.data.sync;

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
