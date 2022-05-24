package ru.mobnius.core.srv;

import android.content.Context;

public abstract class BaseListener {

    protected final Context mContext;
    protected final long mUserId;

    public BaseListener(Context context, long userId) {
        mContext = context;
        mUserId = userId;
    }

    public Context getContext() {
        return mContext;
    }

    public long getUserId() {
        return mUserId;
    }
}
