package ru.mobnius.core.ui.form;

import ru.mobnius.core.ui.form.validator.OnValidatorListeners;

public interface OnValidationListeners {
    /**
     * Добавление поля для проверки на валидность
     * @param validator валидатор
     */
    void addValidator(OnValidatorListeners validator);

    /**
     * Удаление поля для проверки на валидность
     * @param validator валидатор
     */
    void removeValidator(OnValidatorListeners validator);

    /**
     * Очистка валидаций
     */
    void removeValidators();

    /**
     * Получение списка проверок
     */
    OnValidatorListeners[] getValidations();
}
