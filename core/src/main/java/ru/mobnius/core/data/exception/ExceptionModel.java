package ru.mobnius.core.data.exception;

import android.content.Context;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.Date;

import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.utils.DateUtil;
import ru.mobnius.core.utils.HardwareUtil;
import ru.mobnius.core.utils.StringUtil;

/**
 * модель ошибки
 */
public class ExceptionModel {

    public static ExceptionModel getInstance(Date date, String message, String group, int code) {
        return new ExceptionModel(date, message, group, code);
    }

    /**
     * Преобразование строки в модель
     * @param json строка в json формате
     * @return Объект для хранения ошибки
     */
    public static ExceptionModel toModel(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            Date dt = DateUtil.convertStringToDate(jsonObject.getString("id"));
            String message = jsonObject.getString("message");
            int code = jsonObject.getInt("code");
            String group = jsonObject.getString("group");
            return ExceptionModel.getInstance(dt, message, group, code);
        } catch (JSONException e) {
            Logger.error("Ошибка преобразования строки в JSONObject для исключения.", e);
            return null;
        } catch (ParseException e) {
            Logger.error("Ошибка преобразования даты для исключения.", e);
        }
        return null;
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
        return String.format("%s%s", fill.toString(), number);
    }

    @Expose
    private String id;

    /**
     * Текст сообщения об ошибке
     */
    @Expose
    private String message;

    @Expose
    private int code;

    @Expose
    private String group;

    /**
     * Дата возникновения ошибки
     */
    private Date date;

    private ExceptionModel(Date date, String message, String group, int code) {
        this.id = DateUtil.convertDateToString(date);
        this.date = date;
        this.message = message;
        this.group = group;
        this.code = code;
    }

    public Date getDate() {
        return date;
    }

    public String getMessage() {
        return message;
    }

    public String getId() {
        return id;
    }

    public int getCode() {
        return code;
    }

    public String getGroup() {
        return group;
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
        return String.format("%s%s%s", getGroup(), codeToString(getCode()), isDebug ? "D" : "E");
    }

    /**
     * Получение дополнительных данных
     * @param context контекст
     * @return строка с данными в формате JSON
     */
    public String getJSONData(Context context) {
        return "{\"c_imei\":\"" + HardwareUtil.getNumber(context) + "\",\"d_current_date\":\"" + DateUtil.convertDateToString(new Date()) + "\", \"c_md5\":\"" + StringUtil.md5(getMessage()) + "\"}";
    }

    @NonNull
    @Override
    public String toString() {
        Gson json = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        return json.toJson(this);
    }
}
