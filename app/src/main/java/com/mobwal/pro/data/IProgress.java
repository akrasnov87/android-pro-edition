package com.mobwal.pro.data;

import com.mobwal.pro.data.utils.transfer.ITransferStatusCallback;

/**
 * Интерфейс для отслеживания процесса синхронизации
 */
public interface IProgress extends ITransferStatusCallback {

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
