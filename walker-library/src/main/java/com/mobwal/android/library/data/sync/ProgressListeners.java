package com.mobwal.android.library.data.sync;

import com.mobwal.android.library.data.sync.util.transfer.TransferStatusListeners;

/**
 * Интерфейс для отслеживания процесса синхронизации
 */
public interface ProgressListeners extends TransferStatusListeners {

    /**
     * Запуск синхронизации
     * @param synchronization объект синхронизации
     */
    void onStart(OnSynchronizationListeners synchronization);

    /**
     * Отмена синхронизации
     * @param synchronization объект синхронизации
     */
    void onStop(OnSynchronizationListeners synchronization);

    /**
     * Прогресс выполнения синхрониации
     * @param synchronization объект синхронизации
     * @param step шаг выполнения
     * @param message текстовое сообщения
     * @param tid Идентификатор транзакции
     */
    void onProgress(OnSynchronizationListeners synchronization, int step, String message, String tid);

    /**
     * обработчсик ошибок
     * @param synchronization объект синхронизации
     * @param step шаг выполнения
     * @param message текстовое сообщения
     * @param tid Идентификатор транзакции
     */
    void onError(OnSynchronizationListeners synchronization, int step, String message, String tid);
}
