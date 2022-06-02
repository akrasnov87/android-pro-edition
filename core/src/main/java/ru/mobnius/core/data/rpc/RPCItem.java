package ru.mobnius.core.data.rpc;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import ru.mobnius.core.utils.DateUtil;

/**
 * Запрос для RPC
 */
public class RPCItem {

    public RPCItem() {
        tid = DateUtil.geenerateTid();
    }

    public RPCItem(String action, String method, SingleItemQuery data){
        this(action+ "." + method, data);
    }

    public RPCItem(String actionMethod, SingleItemQuery data) {
        this();
        String[] items = actionMethod.split("\\.");
        this.action = items[0];
        this.method = items[1];
        SingleItemQuery[] itemQueries = new SingleItemQuery[1];
        if(data != null) {
            itemQueries[0] = data;
        }
        this.data = new Object[1];

        this.data[0] = itemQueries;
    }

    public RPCItem(String action, String method, QueryData queryData) {
        this();

        this.action = action;
        this.method = method;
        this.data = new Object[1];
        this.data[0] = queryData;
    }

    @Expose
    public String schema;

    /**
     * Действие
     */
    @Expose
    public String action;
    /**
     * Метод
     */
    @Expose
    public String method;
    /**
     * данные для передачи
     */
    @Expose
    public Object[] data;
    /**
     * тип, всегда rpc
     */
    @Expose
    public String type = "rpc";
    /**
     * код запроса. в рамка группы должен быть уникален
     */
    @Expose
    public int tid;

    @Expose
    public Double change;

    public String getFunctionName() {
        return "of_mui_" + action;
    }

    public String toJsonString() {
        Gson gson = new GsonBuilder().serializeNulls().excludeFieldsWithoutExposeAnnotation().create();
        return gson.toJson(this);
    }

    /**
     * Объект для добавления записей
     * @param action сущность
     * @param items список объектов
     * @return объект
     */
    public static RPCItem addItems(String action, Object[] items){
        RPCItem rpcItem = new RPCItem();
        rpcItem.method = "Add";
        rpcItem.action = action;
        rpcItem.data = new Object[1];
        rpcItem.data[0] = items;

        return rpcItem;
    }

    /**
     * Объект для обновления записей
     * @param action сущность
     * @param items список объектов
     * @return объект
     */
    public static RPCItem updateItems(String action, Object[] items){
        RPCItem rpcItem = new RPCItem();
        rpcItem.method = "Update";
        rpcItem.action = action;
        rpcItem.data = new Object[1];
        rpcItem.data[0] = items;

        return rpcItem;
    }

    /**
     * Объект для удаления записей
     * @param action сущность
     * @param items список объектов
     * @return объект
     */
    public static RPCItem deleteItems(String action, Object[] items){
        RPCItem rpcItem = new RPCItem();
        rpcItem.method = "Delete";
        rpcItem.action = action;
        rpcItem.data = new Object[1];
        rpcItem.data[0] = items;

        return rpcItem;
    }

    /**
     * Объект для добавления записи
     * @param action сущность
     * @param item объект
     * @return объект
     */
    public static RPCItem addItem(String action, Object item) {
        RPCItem rpcItem = new RPCItem();
        rpcItem.method = "AddOrUpdate";
        rpcItem.action = action;
        rpcItem.data = new Object[1];
        Object[] array = new Object[1];
        array[0] = item;
        rpcItem.data[0] = array;

        return rpcItem;
    }

    /**
     * Объект для удаления записb
     *
     * @param action сущность
     * @param item   список объектов
     * @return объект
     */
    public static RPCItem deleteItem(String action, Object item) {
        RPCItem rpcItem = new RPCItem();
        rpcItem.method = "Delete";
        rpcItem.action = action;
        rpcItem.data = new Object[1];
        Object[] array = new Object[1];
        array[0] = item;
        rpcItem.data[0] = array;

        return rpcItem;
    }
}
