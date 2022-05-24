package ru.mobnius.core.data.forms;

/**
 * Результат обработки формы
 */
public class FormResult {
    private boolean mIsSuccess;
    private String mMessage;

    /**
     * Обработка формы завершено удачно
     * @param message текст сообщения
     * @return результат
     */
    public static FormResult onSuccess(String message) {
        return new FormResult(true, message);
    }

    /**
     * Обработка формы завершено удачно
     * @return результат
     */
    public static FormResult onSuccess() {
        return onSuccess(null);
    }

    /**
     * Обработка формы завершено с ошибкой
     * @param message текст сообщения
     * @return результат
     */
    public static FormResult onFail(String message) {
        return new FormResult(false, message);
    }

    /**
     * Обработка формы завершено с ошибкой
     * @return результат
     */
    public static FormResult onFail() {
        return onFail(null);
    }

    /**
     *
     * @param success статус
     * @param message текст сообщения
     */
    public FormResult(boolean success, String message) {
        mIsSuccess = success;
        mMessage = message;
    }

    /**
     * Статус обработки результата
     * @return результат
     */
    public boolean isSuccess() {
        return mIsSuccess;
    }

    /**
     * Получение текста сообщения
     * @return результат
     */
    public String getMessage() {
        return mMessage;
    }
}
