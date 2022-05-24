package ru.mobnius.core.data;

public class Meta {

    public static final int OK = 200;
    public static final int NOT_FOUND = 404;
    public static final int ERROR_SERVER = 500;
    public static final int NOT_AUTHORIZATION = 401;

    private int mStatus;
    private String mMessage;

    public Meta(int status, String message) {
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
