package com.mobwal.pro.sync;

import androidx.annotation.NonNull;

import java.util.Date;

public class SynchronizationLogItem {

    public SynchronizationLogItem(@NonNull String message, boolean isError) {
        d_date = new Date();
        c_message = message;
        b_error = isError;
    }

    public Date d_date;
    public String c_message;
    public boolean b_error;
}
