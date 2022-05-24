package ru.mobnius.core;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.credentials.BasicUser;

public abstract class BaseTest {
    private BasicCredentials mCredentials;
    private Context mContext;

    public BaseTest() {
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mCredentials = new BasicCredentials(GlobalSettings.DEFAULT_USER_NAME, GlobalSettings.DEFAULT_USER_PASSWORD);
    }

    public Context getContext() {
        return mContext;
    }

    public BasicCredentials getCredentials() {
        return mCredentials;
    }

    public BasicUser getBasicUser() {
        return new BasicUser(getCredentials(), 4, ".inspector.");
    }

    public String getBaseUrl() {
        return "http://192.168.1.68:3000";
    }
}
