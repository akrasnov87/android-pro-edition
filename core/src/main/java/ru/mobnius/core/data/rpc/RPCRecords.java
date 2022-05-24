package ru.mobnius.core.data.rpc;

import com.google.gson.JsonObject;

/**
 * результат с записями
 */
public class RPCRecords {
    /**
     * список записей
     */
    public JsonObject[] records;

    /**
     * количество записей
     */
    public int total;
}
