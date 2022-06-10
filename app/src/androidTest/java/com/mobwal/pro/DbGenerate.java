package com.mobwal.pro;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.mobwal.android.library.SimpleFileManager;
import com.mobwal.android.library.authorization.credential.BasicCredential;
import com.mobwal.android.library.authorization.credential.BasicUser;

/**
 * Вспомогательный класс для работы с БД
 */
public abstract class DbGenerate {
    private final Context mContext;
    private final WalkerSQLContext mSQLContext;
    private final SimpleFileManager mFileManager;

    public DbGenerate() {
        String dbName = getClass().getName();
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mSQLContext = new WalkerSQLContext(mContext, dbName);
        BasicCredential credentials = getCredentials();
        mFileManager = new SimpleFileManager(mContext.getFilesDir(), credentials);
    }

    public SimpleFileManager getFileManager() {
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
        return Names.getConnectUrl();
    }

    public static BasicCredential getCredentials() {
        return new BasicCredential("user", "1234");
    }

    public static BasicUser getBasicUser() {
        return new BasicUser(getCredentials(), 4, ".user.");
    }
}
