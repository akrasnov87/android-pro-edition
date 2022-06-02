package com.mobwal.android.library.data;

import androidx.annotation.NonNull;

public class Meta {

    public static final int OK = 200;
    public static final int NOT_FOUND = 404;
    public static final int ERROR_SERVER = 500;
    public static final int NOT_AUTHORIZATION = 401;

    private final int mStatus;
    private final String mMessage;

    public Meta(int status, @NonNull String message) {
        mStatus = status;
        mMessage = message;
    }

    public boolean isSuccess() {
        return mStatus == OK;
    }

    public int getStatus() {
        return mStatus;
    }

    public String getMessage() {
        return mMessage;
    }
}
