package com.mobwal.android.library.exception;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.mobwal.android.library.Constants;
import com.mobwal.android.library.PrefManager;
import com.mobwal.android.library.util.StringUtil;

import java.util.Date;
import java.util.Objects;

public class MyUncaughtExceptionHandler
        implements Thread.UncaughtExceptionHandler {

    /**
     * был перехвачен
     */
    private static boolean intercept = false;

    private final Thread.UncaughtExceptionHandler oldHandler;

    private final String group;
    private final int code;
    private final Context mContext;

    /**
     *
     *
     * @param context контекст
     * @param oldHandler Обработчик
     * @param group группа исключения IExceptionGroup
     * @param code код исключения IExceptionCode
     */
    public MyUncaughtExceptionHandler(@NonNull Context context, Thread.UncaughtExceptionHandler oldHandler, @NonNull String group, int code) {
        this.oldHandler = oldHandler;
        this.group = group;
        this.code = code;
        mContext = context;
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        try {
            if(!intercept) {
                Log.d(Constants.TAG, "Перехвачено исключение от группы " + group + ", код " + FaceException.codeToString(code));
                String exceptionString = StringUtil.exceptionToString(e);
                FaceException exceptionModel = new FaceException(new Date(), exceptionString, group, code);

                boolean isDebug = new PrefManager(mContext).get(Constants.DEBUG, false);
                Log.d(Constants.TAG, "Запись исключения " + exceptionModel.getExceptionCode(isDebug) + " в файл.");
                FaceExceptionSingleton.getInstance(mContext).writeBytes(exceptionModel.getFileName(), exceptionModel.toString().getBytes());
                Log.d(Constants.TAG, "Исключение " + exceptionModel.getExceptionCode(isDebug) + " записано в файл.");
            }
        } catch (Exception exc) {
            intercept = false;
            Log.d(Constants.TAG, Objects.requireNonNull(exc.getMessage()));
        } finally {
            intercept = true;
            if (oldHandler != null) {
                oldHandler.uncaughtException(t, e);
            } else {
                System.exit(2);
            }
        }
    }
}