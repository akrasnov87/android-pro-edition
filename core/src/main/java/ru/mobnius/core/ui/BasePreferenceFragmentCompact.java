package ru.mobnius.core.ui;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import ru.mobnius.core.data.exception.IExceptionGroup;
import ru.mobnius.core.data.exception.IExceptionIntercept;
import ru.mobnius.core.data.exception.MyUncaughtExceptionHandler;

public abstract class BasePreferenceFragmentCompact extends PreferenceFragmentCompat
        implements IExceptionIntercept {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onExceptionIntercept();
    }

    @Override
    public void onExceptionIntercept() {
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler(), getExceptionGroup(), getExceptionCode(), getContext()));
    }

    @Override
    public String getExceptionGroup() {
        return IExceptionGroup.SETTING;
    }
}
