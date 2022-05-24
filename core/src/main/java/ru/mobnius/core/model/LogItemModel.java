package ru.mobnius.core.model;

import java.util.Date;

public class LogItemModel {
    public LogItemModel(String message, boolean isError) {
        d_date = new Date();
        c_message = message;
        b_error = isError;
    }

    public Date d_date;
    public String c_message;
    public boolean b_error;
}
