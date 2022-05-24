package ru.mobnius.core.adapter.task;

import android.os.AsyncTask;

import java.util.List;

import ru.mobnius.core.data.authorization.Authorization;
import ru.mobnius.core.data.configuration.ConfigurationSetting;
import ru.mobnius.core.data.configuration.ConfigurationSettingUtil;
import ru.mobnius.core.data.configuration.DefaultPreferencesManager;
import ru.mobnius.core.data.credentials.BasicCredentials;

public class ConfigurationAsyncTask extends AsyncTask<Void, Void, Boolean> {

    private String mBaseUrl;
    private final OnConfigurationLoadedListener mListener;

    public ConfigurationAsyncTask(String baseUrl, OnConfigurationLoadedListener listener) {
        mListener = listener;
        mBaseUrl = baseUrl;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
        if(Authorization.getInstance().isAuthorized()) {
            BasicCredentials credentials = Authorization.getInstance().getUser().getCredentials();

            try {
                List<ConfigurationSetting> configurationSettings = ConfigurationSettingUtil.getSettings(mBaseUrl, credentials);
                if (configurationSettings != null) {
                    return DefaultPreferencesManager.getInstance().updateSettings(configurationSettings);
                }
            } catch (Exception ignore) {

            }
        }

        return false;
    }

    @Override
    protected void onPostExecute(Boolean value) {
        mListener.onConfigurationLoaded(value);
    }

    public interface OnConfigurationLoadedListener {
        void onConfigurationLoaded(boolean configRefreshed);
    }
}
