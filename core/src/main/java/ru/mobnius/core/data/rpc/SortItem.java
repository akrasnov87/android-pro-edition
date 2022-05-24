package ru.mobnius.core.data.rpc;

import com.google.gson.annotations.Expose;

public class SortItem {
    public final static String ASC = "ASC";
    public final static String DESC = "DESC";

    /**
     * сортировка
     * @param property свойство
     * @param direction тип сортировки ASC, DESC
     */
    public SortItem(String property, String direction) {
        this.direction = direction;
        this.property = property;
    }

    /**
     * Сортировка по убыванию
     * @param property свойтсва для сортировки
     */
    public SortItem(String property) {
        this(property, DESC);
    }

    @Expose
    public String property;

    @Expose
    public String direction;
}
