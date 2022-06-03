package com.mobwal.android.library.exception;

import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.mobwal.android.library.Constants;
import com.mobwal.android.library.util.DateUtil;
import com.mobwal.android.library.util.VersionUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * модель ошибки
 */
public class FaceException {

    /**
     * Преобразование строки в модель
     * @param json строка в json формате
     * @return Объект для хранения ошибки
     */
    public static FaceException toFace(@NonNull String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Date dt = DateUtil.convertStringToSystemDate(jsonObject.getString("id"));
            String message = jsonObject.getString("message");
            int code = jsonObject.getInt("code");
            String group = jsonObject.getString("group");

            FaceException faceException = new FaceException(dt == null ? new Date() : dt, message, group, code);
            faceException.version = jsonObject.getString("version");
            faceException.model = jsonObject.getString("model");
            faceException.architecture = jsonObject.getString("architecture");
            faceException.sdk = jsonObject.getString("sdk");
            faceException.release = jsonObject.getString("release");

            return faceException;
        } catch (JSONException e) {
            Log.e(Constants.TAG, "Ошибка преобразования строки в JSONObject для исключения.", e);
            return null;
        }
    }

    /**
     * Преобразование кода в строку
     * @param code числовой код
     * @return выходной код
     */
    public static String codeToString(int code) {
        String number = String.valueOf(code);
        StringBuilder fill = new StringBuilder();
        for(int i = number.length(); i < 3; i++){
            fill.append("0");
        }
        return String.format("%s%s", fill, number);
    }

    @Expose
    public String id;

    /**
     * Текст сообщения об ошибке
     */
    @Expose
    public String message;

    @Expose
    public int code;

    @Expose
    public String group;

    /**
     * Дата возникновения ошибки
     */
    public Date date;

    @Expose
    public String version;

    @Expose
    public String architecture;

    @Expose
    public String model;

    @Expose
    public String sdk;

    @Expose
    public String release;

    public FaceException(@NonNull Date date, @NonNull String message, @NonNull String group, int code) {
        this.id = DateUtil.convertDateToSystemString(date);
        this.date = date;
        this.message = message;
        this.group = group;
        this.code = code;
    }

    /**
     *
     * @param context контекст
     * @param date дата возникновения ошибки
     * @param message текст ошибки
     * @param group группа
     * @param code код ошибки
     */
    public FaceException(@NonNull Context context, @NonNull Date date, @NonNull String message, @NonNull String group, int code) {
        this.id = DateUtil.convertDateToSystemString(date);
        this.date = date;
        this.message = message;
        this.group = group;
        this.code = code;

        this.version = VersionUtil.getVersionName(context);
        this.architecture = System.getProperty("os.arch");
        this.model = Build.MODEL;
        this.sdk = String.valueOf(Build.VERSION.SDK_INT);
        this.release = Build.VERSION.RELEASE;
    }

    /**
     * Имя файла для хранения исключения
     * @return имя файла
     */
    public String getFileName(){
        return String.format("%s.exc", this.id);
    }

    /**
     * Получение кода ошибки
     * @param isDebug включен ли режим отладки
     * @return код ошибки
     */
    public String getExceptionCode(boolean isDebug) {
        return String.format("%s%s%s", group, code, isDebug ? "D" : "E");
    }

    @NonNull
    @Override
    public String toString() {
        Gson json = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        return json.toJson(this);
    }
}
