package ru.mobnius.core.data.configuration;

import android.content.Context;

import ru.mobnius.core.data.GlobalSettings;

/**
 * Менеджер настроек по умолчанию для всего приложения
 */
public class DefaultPreferencesManager extends AbstractPreferencesManager {

    public final static String NAME = "__default";

    public static final String BASE_URL = "BASE_URL";
    public static final String VIRTUAL_DIR_PATH = "VIRTUAL_DIR_PATH";
    public static final String ENVIRONMENT = "ENVIRONMENT";

    private static DefaultPreferencesManager preferencesManager;

    public static DefaultPreferencesManager getInstance() {
        return preferencesManager;
    }

    public static DefaultPreferencesManager createInstance(Context context, String preferenceName) {
        return preferencesManager = new DefaultPreferencesManager(context, preferenceName);
    }

    protected DefaultPreferencesManager(Context context, String preferenceName){
        super(context, preferenceName);
    }

    public String getBaseUrl(String defaultValue) { return getStringValue(BASE_URL, defaultValue);}

    public void setBaseUrl(String value) { getSharedPreferences().edit().putString(BASE_URL, value).apply();}

    public String getVirtualDirPath(String defaultValue) { return getStringValue(VIRTUAL_DIR_PATH, defaultValue);}

    public void setVirtualDirPath(String value) { getSharedPreferences().edit().putString(VIRTUAL_DIR_PATH, value).apply();}

    public String getEnvironment(String defaultValue) { return getStringValue(ENVIRONMENT, defaultValue);}

    public void setEnvironment(String value) { getSharedPreferences().edit().putString(ENVIRONMENT, value).apply();}

    public void clearConnection() {
        getSharedPreferences().edit().remove(BASE_URL).apply();
        getSharedPreferences().edit().remove(VIRTUAL_DIR_PATH).apply();
        getSharedPreferences().edit().remove(ENVIRONMENT).apply();
    }
}
