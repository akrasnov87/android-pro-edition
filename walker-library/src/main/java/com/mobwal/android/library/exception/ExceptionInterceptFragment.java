package com.mobwal.android.library.exception;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.mobwal.android.library.exception.ExceptionInterceptListeners;
import com.mobwal.android.library.exception.MyUncaughtExceptionHandler;

public abstract class ExceptionInterceptFragment extends Fragment
        implements ExceptionInterceptListeners {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onExceptionIntercept();
    }

    /**
     * Обработчик перехвата ошибок
     */
    public void onExceptionIntercept() {
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(requireContext(), Thread.getDefaultUncaughtExceptionHandler(), getExceptionGroup(), getExceptionCode()));
    }

    public String getExceptionGroup() {
        return "FRAGMENT";
    }
}
