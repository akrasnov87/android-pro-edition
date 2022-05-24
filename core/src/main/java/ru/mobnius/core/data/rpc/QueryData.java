package ru.mobnius.core.data.rpc;

import com.google.gson.annotations.Expose;

public class QueryData {

    public final static String FILTER_AND = "AND";
    public final static String FILTER_OR = "OR";

    public QueryData() {
        query = "";
        page = 1;
        start = 0;
        limit = 25;
    }

    /**
     * Псевдоним для переопределения результата, может буть null
     */
    @Expose
    public String alias;

    @Expose
    public String select;

    @Expose
    public String query;

    @Expose
    public int page;

    @Expose
    public int start;

    @Expose
    public int limit;

    /**
     * Фильтрация по правилам RPC
     * Элементом массива может являтся строка FILTER_*, либо FilterItem
     */
    @Expose
    public Object[] filter;

    /**
     * Сортировка
     */
    @Expose
    public SortItem[] sort;
}
