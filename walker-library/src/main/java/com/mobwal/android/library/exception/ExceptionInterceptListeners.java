package com.mobwal.android.library.exception;

/**
 * Интерфейс перехвата ошибок
 */
public interface ExceptionInterceptListeners {
    /**
     * Обработчик перехвата ошибок
     */
    void onExceptionIntercept();

    /**
     * Группа ошибки из IExceptionGroup
     * @return строка
     */
    String getExceptionGroup();

    /**
     * Числовой код ошибки из IExceptionCode
     * @return строка
     */
    int getExceptionCode();
}
