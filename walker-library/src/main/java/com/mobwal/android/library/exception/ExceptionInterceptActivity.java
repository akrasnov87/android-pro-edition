package com.mobwal.android.library.exception;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * абстрактный класс для реализации обработчиков ошибок
 */
public abstract class ExceptionInterceptActivity extends AppCompatActivity
        implements IExceptionIntercept {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // предназначено для привязки перехвата ошибок
        onExceptionIntercept();
    }

    public void onExceptionIntercept() {
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(this, Thread.getDefaultUncaughtExceptionHandler(), getExceptionGroup(), getExceptionCode()));
    }

    @Override
    public String getExceptionGroup() {
        return "UI";
    }
}
