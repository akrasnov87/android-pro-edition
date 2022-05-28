package ru.mobnius.core.data.configuration;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.mobnius.core.data.RequestManager;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.rpc.RPCResult;
import ru.mobnius.core.data.rpc.SingleItemQuery;

public class ConfigurationSettingUtil {
    public final static String ACTION = "shell";
    public final static String METHOD = "getServerTime";

    /**
     * Преобразование значения настройки в Integer
     * @param configurationSetting настройка
     * @return значение
     */
    public static Integer getIntegerValue(ConfigurationSetting configurationSetting) {
        if(configurationSetting != null && configurationSetting.type.equals(ConfigurationSetting.INTEGER)) {
            try {
                return Integer.parseInt(configurationSetting.value);
            }catch (NumberFormatException ignore) {
                return null;
            }
        }
        return null;
    }

    /**
     * Преобразование значения настройки в Double
     * @param configurationSetting настройка
     * @return значение
     */
    public static Double getDoubleValue(ConfigurationSetting configurationSetting) {
        if(configurationSetting != null && configurationSetting.type.equals(ConfigurationSetting.REAL)) {
            try {
                return Double.parseDouble(configurationSetting.value);
            }catch (NumberFormatException ignore) {
                return null;
            }
        }
        return null;
    }

    /**
     * Преобразование значения настройки в String
     * @param configurationSetting настройка
     * @return значение
     */
    public static String getStringValue(ConfigurationSetting configurationSetting) {
        if(configurationSetting != null && configurationSetting.type.equals(ConfigurationSetting.TEXT)) {
            return configurationSetting.value;
        }
        return null;
    }

    /**
     * Преобразование значения настройки в Boolean
     * @param configurationSetting настройка
     * @return значение
     */
    public static Boolean getBooleanValue(ConfigurationSetting configurationSetting) {
        if(configurationSetting != null && configurationSetting.value != null && configurationSetting.type.equals(ConfigurationSetting.BOOLEAN)) {
            switch (configurationSetting.value.toLowerCase()) {
                case "0":
                case "false":
                    return false;

                case "1":
                case "true":
                    return true;
            }
        }
        return null;
    }

    /**
     * Чтение списка настроек из результата запроса
     * @param result результат запроса
     * @return список настроек
     */
    public static List<ConfigurationSetting> getConfigurationSettings(JsonObject result) {
        if(result == null){
            return null;
        }
        List<ConfigurationSetting> configurationSettings = new ArrayList<>();

        for (String key : result.keySet()) {
            ConfigurationSetting configurationSetting = new ConfigurationSetting();

            JsonObject jsonObject = result.getAsJsonObject(key);

            for (String name : jsonObject.keySet()) {
                String val = null;
                if(!jsonObject.get(name).isJsonNull()) {
                    val = jsonObject.get(name).getAsString();
                }
                switch (name) {
                    case "key":
                        configurationSetting.key = val;
                        break;

                    case "value":
                        configurationSetting.value = val;
                        break;

                    case "label":
                        configurationSetting.label = val;
                        break;

                    case "summary":
                        configurationSetting.summary = val;
                        break;

                    case "type":
                        configurationSetting.type = val;
                        break;
                }
            }

            configurationSettings.add(configurationSetting);
        }

        if(configurationSettings.size() > 0){
            return configurationSettings;
        }
        return null;
    }

    /**
     * Получение настроек от сервера
     * @return Возвращается список настроек
     */
    public static List<ConfigurationSetting> getSettings(String baseUrl,  BasicCredentials credentials) {
        try {
            RPCResult[] results = RequestManager.rpc(baseUrl, credentials.getToken(), ConfigurationSettingUtil.ACTION, ConfigurationSettingUtil.METHOD, new SingleItemQuery(new String[0]));
            if(results[0].isSuccess()) {
                return getConfigurationSettings(results[0].result.records[0]);
            }
        }catch (IOException ignore) {

        }
        return null;
    }
}
