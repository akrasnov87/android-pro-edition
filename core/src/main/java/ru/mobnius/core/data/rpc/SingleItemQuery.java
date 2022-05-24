package ru.mobnius.core.data.rpc;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;

import ru.mobnius.core.data.synchronization.BaseSynchronization;

/**
 * Параметры передаваемые в одиночных запросах RPC
 */
public class SingleItemQuery {

    public SingleItemQuery(Object... obj) {
        this.params = obj;
        this.limit = BaseSynchronization.MAX_COUNT_IN_QUERY;
    }

    public SingleItemQuery(String... obj) {
        this.limit = BaseSynchronization.MAX_COUNT_IN_QUERY;
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
