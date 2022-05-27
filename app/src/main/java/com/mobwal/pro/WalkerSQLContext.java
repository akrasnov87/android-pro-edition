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

public class WalkerSQLContext extends SQLContext {

    public WalkerSQLContext(Context context) {
        super(context);
    }

    public WalkerSQLContext(Context context, String dbName) {
        super(context, dbName);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();

        try {
            db.execSQL(getCreateQuery(new Template(), "id"));
            db.execSQL(getCreateQuery(new Setting(), "id"));
            db.execSQL(getCreateQuery(new Route(), "id"));
            db.execSQL(getCreateQuery(new Point(), "id"));
            db.execSQL(getCreateQuery(new Result(), "id"));
            db.execSQL(getCreateQuery(new Attachment(), "id"));

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
