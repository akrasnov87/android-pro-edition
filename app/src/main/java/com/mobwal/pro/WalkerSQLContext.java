package com.mobwal.pro;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mobwal.pro.models.db.Attachment;
import com.mobwal.pro.models.db.Audit;
import com.mobwal.pro.models.db.MobileDevice;
import com.mobwal.pro.models.db.Point;
import com.mobwal.pro.models.db.Result;
import com.mobwal.pro.models.db.Route;
import com.mobwal.pro.models.db.Setting;
import com.mobwal.pro.models.db.Template;
import com.mobwal.android.library.sql.SQLContext;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WalkerSQLContext extends SQLContext {

    /**
     * Текущая версия БД
     */
    public static final int DATABASE_VERSION = 1;

    public WalkerSQLContext(@NotNull Context context, @NotNull String dbName) {
        super(context, dbName, DATABASE_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    /**
     * Получение списка таблиц, которые будут храниться в SQLite
     * @return список таблиц
     */
    public Object[] getTables() {
        List<Object> objectList = new ArrayList<>();
        objectList.add(new Template());
        objectList.add(new Setting());
        objectList.add(new Route());
        objectList.add(new Point());
        objectList.add(new Result());
        objectList.add(new Attachment());
        objectList.add(new Audit());
        objectList.add(new MobileDevice());

        return objectList.toArray();
    }
}
