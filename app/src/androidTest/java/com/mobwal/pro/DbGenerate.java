package com.mobwal.pro;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.mobwal.android.library.FileManager;
import ru.mobnius.core.data.GlobalSettings;
import com.mobwal.android.library.authorization.credential.BasicCredential;
import com.mobwal.android.library.authorization.credential.BasicUser;

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
        BasicCredential credentials = getCredentials();
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

    public static BasicCredential getCredentials() {
        return new BasicCredential("test", "1234");
    }

    public static BasicUser getBasicUser() {
        return new BasicUser(getCredentials(), 4, ".user.");
    }
}
