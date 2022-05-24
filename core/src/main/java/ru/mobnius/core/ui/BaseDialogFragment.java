package ru.mobnius.core.ui;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import ru.mobnius.core.data.exception.IExceptionIntercept;
import ru.mobnius.core.data.exception.MyUncaughtExceptionHandler;
import ru.mobnius.core.data.exception.IExceptionGroup;

/**
 * Базовый класс для DialogFragment
 */
public abstract class BaseDialogFragment extends DialogFragment
        implements IExceptionIntercept {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onExceptionIntercept();
    }

    /**
     * Обработчик перехвата ошибок
     */
    public void onExceptionIntercept() {
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler(), getExceptionGroup(), getExceptionCode(), getContext()));
    }

    public String getExceptionGroup() {
        return IExceptionGroup.DIALOG;
    }
}
