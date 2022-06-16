package com.mobwal.pro;

import android.content.Context;

import androidx.annotation.Nullable;
import androidx.test.platform.app.InstrumentationRegistry;

import com.mobwal.android.library.SimpleFileManager;
import com.mobwal.android.library.authorization.credential.BasicCredential;
import com.mobwal.android.library.authorization.credential.BasicUser;
import com.mobwal.android.library.data.DbOperationType;
import com.mobwal.pro.models.db.Attachment;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Date;

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

    public static String getDefaultRouteId() {
        return "e6c4bd3d-afc4-4669-ae5b-a79082f8b8bd";
    }

    public static BasicCredential getCredentials() {
        return new BasicCredential("user", "1234");
    }

    public static BasicUser getBasicUser() {
        return new BasicUser(getCredentials(), 4, ".user.", "127.0.0.1");
    }

    /**
     * Сохранение файла
     *
     * @param fileName имя файла
     * @param bytes  массив байтов
     * @return файл
     */
    @Nullable
    public Attachment saveFile(@NotNull String fileName, @NotNull byte[] bytes) {
        try {
            getFileManager().writeBytes(fileName, bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        Attachment file = new Attachment();
        file.fn_route = getDefaultRouteId();
        file.c_name = fileName;
        file.d_date = new Date();
        file.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;

        getSQLContext().insert(file);
        return file;
    }

    public void destroy() {
        mFileManager.deleteFolder();
        mSQLContext.trash();
    }
}
