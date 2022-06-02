package ru.mobnius.core.ui;

import android.widget.LinearLayout;

import java.util.Objects;

import ru.mobnius.core.data.exception.IExceptionGroup;
import ru.mobnius.core.data.exception.IExceptionIntercept;
import ru.mobnius.core.data.exception.MyUncaughtExceptionHandler;

/**
 * Базовый класс для BaseDialogFragment
 */
public abstract class BaseFullDialogFragment extends BaseDialogFragment {
    @Override
    public void onStart() {
        super.onStart();

        Objects.requireNonNull(Objects.requireNonNull(Objects.requireNonNull(getDialog())).getWindow()).setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    }
}
