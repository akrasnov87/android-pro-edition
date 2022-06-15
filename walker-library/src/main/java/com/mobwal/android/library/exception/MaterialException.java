package com.mobwal.android.library.exception;

import android.app.Activity;
import android.content.res.Configuration;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.mobwal.android.library.Constants;
import com.mobwal.android.library.util.DateUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * модель ошибки
 */
public class MaterialException {

    /**
     * Преобразование строки в модель
     * @param json строка в json формате
     * @return Объект для хранения ошибки
     */
    public static MaterialException toFace(@NonNull String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Date dt = DateUtil.convertStringToSystemDate(jsonObject.getString("id"));
            String message = jsonObject.getString("message");
            int code = jsonObject.getInt("code");
            String group = jsonObject.getString("group");
            int orientation = jsonObject.getInt("orientation");

            return new MaterialException(dt == null ? new Date() : dt, message, group, code, orientation);
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

    public MaterialException(@NonNull Date date, @NonNull String message, @NonNull String group, int code, int orientation) {
        this.id = DateUtil.convertDateToSystemString(date);
        this.date = date;
        this.message = message;
        this.group = group;
        this.code = code;
        this.orientation = orientation;
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

    @Expose
    public String label;

    @Expose
    public int orientation;

    /**
     * Дата возникновения ошибки
     */
    public Date date;

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
        String orient = "";
        switch (orientation) {
            case android.content.res.Configuration.ORIENTATION_PORTRAIT:
                orient = "P";
                break;

            case android.content.res.Configuration.ORIENTATION_LANDSCAPE:
                orient = "L";
                break;

            case android.content.res.Configuration.ORIENTATION_SQUARE:
                orient = "S";
                break;

            case android.content.res.Configuration.ORIENTATION_UNDEFINED:
                orient = "U";
                break;
        }
        return String.format("%s%s%s%s", group, code, isDebug ? "D" : "E", orient);
    }

    @NonNull
    @Override
    public String toString() {
        Gson json = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        return json.toJson(this);
    }
}
