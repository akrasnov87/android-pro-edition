package com.mobwal.android.library.data.rpc;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

/**
 * Параметры передаваемые в одиночных запросах RPC
 */
public class SingleItemQuery {

    public SingleItemQuery(int limit, Object... obj) {
        this.params = obj;
        this.limit = limit;
    }

    public SingleItemQuery(int limit, String... obj) {
        this.limit = limit;
        this.params = obj;
    }

    public void setFilter(Object[] items) {
        filter = items;
    }

    @Expose
    private Object[] filter;

    /**
     * дополнительные параметры. Применяется для вызова одиночных метод
     */
    @Expose
    private Object[] params;

    @Expose
    public int limit;

    public String toJsonString() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
