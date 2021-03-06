package com.mobwal.android.library.data.sync;

import android.app.Activity;

import androidx.annotation.NonNull;

import com.mobwal.android.library.socket.SocketManager;
import com.mobwal.android.library.sql.SQLContext;

import java.util.List;

/**
 * интерфейс механизма синхронизации
 */
public interface OnSynchronizationListeners {
    /**
     * имя синхронизации
     * @return возвращается имя синхронизации
     */
    String getName();

    /**
     * идентификатор пользователя
     * @return идентификатор пользователя
     */
    long getUserID();

    /**
     * статус завершения синхронизации
     * @return статус
     */
    FinishStatus getFinishStatus();

    /**
     * Изменение статуса завершения синхронизации
     * @param status статус завершения синхронизации
     */
    void changeFinishStatus(FinishStatus status);

    /**
     * Список сущностей
     * @return возвращается список сущностей по которым разрешена отправка на сервер
     */
    List<Entity> getEntityToList();

    /**
     * Список сущностей
     * @param tid идентификатор транзакции
     * @return возвращается список сущностей с tid
     */
    Entity[] getEntities(String tid);

    /**
     * Запуск на выполение
     * @param progress результат выполнения
     */
    void start(@NonNull SocketManager socketManager, @NonNull ProgressListeners progress);

    /**
     * Принудительная остановка выполнения
     */
    void stop();

    /**
     * обработчик ошибок
     * @param step шаг см. IProgressStep
     * @param e исключение
     * @param tid идентификатор транзакции
     */
    void onError(int step, Exception e, String tid);

    /**
     * обработчик ошибок
     * @param step шаг см. IProgressStep
     * @param message текстовое сообщение
     * @param tid идентификатор транзакции
     */
    void onError(int step, String message, String tid);

    SQLContext getContext();
}
