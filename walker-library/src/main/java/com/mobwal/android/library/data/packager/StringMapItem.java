package com.mobwal.android.library.data.packager;

import androidx.annotation.NonNull;

import com.google.gson.annotations.Expose;

public class StringMapItem {
    @Expose
    public int length;
    @Expose
    public String name;

    public StringMapItem(@NonNull String name, int length) {
        this.name = name;
        this.length = length;
    }
}
