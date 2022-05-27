package com.mobwal.pro.data;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.mobwal.pro.WalkerSQLContext;

import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.credentials.BasicUser;

/**
 * Вспомогательный класс для работы с БД
 */
public abstract class DbGenerate {
    private final Context mContext;
    private final WalkerSQLContext mSQLContext;

    public DbGenerate() {
        String dbName = getClass().getName();
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mSQLContext = new WalkerSQLContext(mContext, dbName);
    }

    /**
     * получение ссылки на подключение к БД
     * @return объект DaoSession
     */
    public WalkerSQLContext getSQLContext() {
        return mSQLContext;
    }

    public Context getContext() {
        return mContext;
    }

    public static String getBaseUrl() {
        return GlobalSettings.getConnectUrl();
    }

    public static BasicCredentials getCredentials() {
        return new BasicCredentials("iserv", "qwe-123+");
    }

    public static BasicUser getBasicUser() {
        return new BasicUser(getCredentials(), 4, ".user.");
    }
}
