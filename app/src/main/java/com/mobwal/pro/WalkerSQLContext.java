package com.mobwal.pro;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mobwal.pro.models.db.attachments;
import com.mobwal.pro.models.db.cd_points;
import com.mobwal.pro.models.db.cd_results;
import com.mobwal.pro.models.db.cd_routes;
import com.mobwal.pro.models.db.cd_settings;
import com.mobwal.pro.models.db.cd_templates;
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
            db.execSQL(getCreateQuery(new cd_templates(), "id"));
            db.execSQL(getCreateQuery(new cd_settings(), "id"));
            db.execSQL(getCreateQuery(new cd_routes(), "id"));
            db.execSQL(getCreateQuery(new cd_points(), "id"));
            db.execSQL(getCreateQuery(new cd_results(), "id"));
            db.execSQL(getCreateQuery(new attachments(), "id"));

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
