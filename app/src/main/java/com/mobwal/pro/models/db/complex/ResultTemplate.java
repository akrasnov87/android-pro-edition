package com.mobwal.pro.models.db.complex;

import android.text.TextUtils;

import androidx.annotation.Nullable;

import java.util.Date;

/**
 * Результат работы, который может быть
 */
public class ResultTemplate {
    public String f_template;
    public String c_template;
    public String c_const;
    public Date d_date;
    public Boolean b_server;

    /**
     * Идентификатор результата
     */
    @Nullable
    public String f_result;

    public boolean isExistsResult() {
        return !TextUtils.isEmpty(f_result);
    }
}
