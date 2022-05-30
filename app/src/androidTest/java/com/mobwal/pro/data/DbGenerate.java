package com.mobwal.pro.data;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.mobwal.pro.WalkerSQLContext;

import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.credentials.BasicUser;

/**
 * Вспомогательный класс для работы с БД
 */
public abstract class DbGenerate {
    private final Context mContext;
    private final WalkerSQLContext mSQLContext;
    private final FileManager mFileManager;

    public DbGenerate() {
        String dbName = getClass().getName();
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mSQLContext = new WalkerSQLContext(mContext, dbName);
        BasicCredentials credentials = getCredentials();
        mFileManager = FileManager.createInstance(credentials, getContext());
    }

    public FileManager getFileManager() {
        return mFileManager;
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
        return new BasicCredentials("test", "qwe-123+");
    }

    public static BasicUser getBasicUser() {
        return new BasicUser(getCredentials(), 4, ".user.");
    }
}
