package com.mobwal.android.library.data.packager;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;
import com.mobwal.android.library.data.rpc.RPCItem;

/**
 * Строковый блок пакета синхронизации
 */
public class StringBlock {

    public StringBlock(@NonNull RPCItem[] to, @NonNull RPCItem[] from) {
        this.to = to;
        this.from = from;
    }

    /**
     * блок данных для отправки
     */
    @Expose
    public RPCItem[] to;

    /**
     * блок данных для получения
     */
    @Expose
    public RPCItem[] from;

    public String toJsonString() {
        Gson json = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        return json.toJson(this);
    }
}
