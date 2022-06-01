package com.mobwal.pro.shared;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.mobwal.android.library.sql.SQLContext;

import java.util.Collections;
import java.util.List;

public class SQLContextProfile extends SQLContext {

    private final List<Object> mTableList = Collections.singletonList(
            new Profile());

    @Override
    public Object[] getTables() {
        return mTableList.toArray();
    }

    public SQLContextProfile(Context context) {
        super(context, "walker", 1);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
