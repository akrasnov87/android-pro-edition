package com.mobwal.pro.models;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.Nullable;

import com.mobwal.pro.R;

/**
 * Информация по точке
 */
public class PointInfo {

    public PointInfo(Context context, String label, String text) {
        this.label = label;
        this.text = TextUtils.isEmpty(text) ? context.getString(R.string.unknown) : text;
    }

    public String label;
    public String text;

    @Nullable
    public String result;

    public boolean server;

    public boolean isResult() {
        return !TextUtils.isEmpty(result);
    }
}
