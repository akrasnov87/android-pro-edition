package ru.mobnius.core.ui;

import android.text.TextWatcher;

public interface OnStringTextWatcher extends TextWatcher {

    /**
     * Обработчик изменения
     * @param id идентификатор
     * @param prevValue пред. значение
     * @param value значение
     */
    void afterStringTextChanged(String id, String prevValue, String value);
}
