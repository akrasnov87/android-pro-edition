package ru.mobnius.core.data.packager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import ru.mobnius.core.data.rpc.RPCItem;

/**
 * Строковый блок пакета синхронизации
 */
public class StringBlock {

    public StringBlock(RPCItem[] to, RPCItem[] from) {
        this.to = to;
        this.from = from;
    }

    StringBlock(RPCItem[] to){
        this(to, new RPCItem[0]);
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
