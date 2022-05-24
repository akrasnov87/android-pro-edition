package ru.mobnius.core.ui;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ru.mobnius.core.data.exception.IExceptionIntercept;
import ru.mobnius.core.data.exception.MyUncaughtExceptionHandler;
import ru.mobnius.core.data.exception.IExceptionGroup;

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
        Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(Thread.getDefaultUncaughtExceptionHandler(), getExceptionGroup(), getExceptionCode(), this));
    }

    @Override
    public String getExceptionGroup() {
        return IExceptionGroup.USER_INTERFACE;
    }
}
