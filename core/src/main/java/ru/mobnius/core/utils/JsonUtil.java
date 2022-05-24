package ru.mobnius.core.utils;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonUtil {
    public final static String EMPTY = "{}";

    public static boolean isEmpty(String json) {
        return json.equals(EMPTY);
    }

    public static String getStringFromJSONObject(JSONObject jsonObject, String name) throws JSONException {
        try {
            String value = jsonObject.has(name) ? jsonObject.getString(name) : "";
            return StringUtil.isEmptyOrNull(value) ? "" : value;
        } catch (JSONException e) {
            return "";
        }
    }

    public static Integer getIntFromJSONObject(JSONObject jsonObject, String name) throws JSONException {
        try {
            return jsonObject.has(name) ? jsonObject.getInt(name) : null;
        } catch (JSONException e) {
            return null;
        }
    }

    public static Boolean getBooleanFromJSONObject(JSONObject jsonObject, String name, boolean _default) throws JSONException {
        try {
            return jsonObject.has(name) ? jsonObject.getBoolean(name) : _default;
        } catch (JSONException e) {
            return _default;
        }
    }

    public static Double geDoubleFromJSONObject(JSONObject jsonObject, String name) throws JSONException {
        try {
            return jsonObject.has(name) ? jsonObject.getDouble(name) : null;
        } catch (JSONException e) {
            return null;
        }
    }
}
