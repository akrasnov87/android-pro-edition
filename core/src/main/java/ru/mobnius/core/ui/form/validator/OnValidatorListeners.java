package ru.mobnius.core.ui.form.validator;

public interface OnValidatorListeners<T> {

    /**
     * Представление
     */
    T getView();

    /**
     * Полечение текстого сообщения об ошибке если поле не валидно
     * @return текст
     */
    String getMessage();

    /**
     * Проверка валидности поля
     * @return валидно поле или нет
     */
    boolean isValid();
}
