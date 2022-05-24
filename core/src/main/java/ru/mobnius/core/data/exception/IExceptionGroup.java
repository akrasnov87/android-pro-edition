package ru.mobnius.core.data.exception;

/**
 * Группы ошибок
 */
public interface IExceptionGroup {
    /**
     * Авторизация
     */
    String AUTHORIZATION = "AUTH";
    /**
     * Интерфейс
     */
    String USER_INTERFACE = "UI";
    /**
     * Синхронизация
     */
    String SYNCHRONIZATION = "SYNC";
    /**
     * Настройки
     */
    String SETTING = "STG";
    /**
     * Фоновые службы
     */
    String SERVICE = "SRV";
    /**
     * Модуль: камера, GPS
     */
    String HARDWARE = "MDL";

    /**
     * На уровне всего приложения
     */
    String APPLICATION = "APP";

    /**
     * Диалоговые окна
     */
    String DIALOG = "UI_DLG";

    /**
     * Неизвестно
     */
    String NONE = "NONE";
}
