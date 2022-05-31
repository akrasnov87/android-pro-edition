package com.mobwal.pro;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mobwal.pro.models.db.Attachment;
import com.mobwal.pro.models.db.Point;
import com.mobwal.pro.models.db.Result;
import com.mobwal.pro.models.db.Route;
import com.mobwal.pro.models.db.Setting;
import com.mobwal.pro.models.db.Template;
import com.mobwal.pro.utilits.SQLContext;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class WalkerSQLContext extends SQLContext {

    public final List<Object> mTableList = Arrays.asList(
            new Template(),
            new Setting(),
            new Route(),
            new Point(),
            new Result(),
            new Attachment());

    public WalkerSQLContext(@NotNull Context context) {
        super(context);
    }

    public WalkerSQLContext(@NotNull Context context, @NotNull String dbName) {
        super(context, dbName);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();

        try {
            for (Object obj: mTableList) {
                db.execSQL(getCreateQuery(obj));
            }
            db.setTransactionSuccessful();
        } catch (Exception ignored) {

        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
