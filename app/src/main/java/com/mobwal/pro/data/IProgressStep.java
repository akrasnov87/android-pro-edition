package com.mobwal.pro.data;

/**
 * шаг выполнения
 */
public interface IProgressStep {
    int NONE = 0;

    /**
     * начальная обработка
     */
    int START = 1;

    /**
     * формирование пакета
     */
    int PACKAGE_CREATE = 2;

    /**
     * Загрузка на сервер
     */
    int UPLOAD = 3;

    /**
     * Загрузка на клиент
     */
    int DOWNLOAD = 4;

    /**
     * восстановление
     */
    int RESTORE = 5;

    /**
     * завершение
     */
    int STOP = 6;
}
