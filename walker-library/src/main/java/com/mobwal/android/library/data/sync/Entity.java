package com.mobwal.android.library.data.sync;

import android.text.TextUtils;

import com.mobwal.android.library.annotation.TableMetaData;
import com.mobwal.android.library.util.ReflectionUtil;

import java.util.UUID;

import com.mobwal.android.library.data.rpc.FilterItem;

public class Entity implements IEntity {

    public TableMetaData meta;

    /**
     * использовать функцию cf_ для получения данных
     */
    public boolean useCFunction;

    public String category;

    /**
     * Идентификатор сущности. Предназначен для работы с пакетами
     */
    public String tid;
    /**
     * Номер изменения
     */
    public Double change;

    /**
     * имя таблицы
     */
    public String tableName;

    /**
     * список колонок для выборки
     */
    public String select = "";

    public String schema = "dbo";

    /**
     * Передача данных на сервер
     */
    public boolean to;

    /**
     * Получение данных от сервера
     */
    public boolean from;
    /**
     * является справочником
     */
    protected boolean isDictionary = false;

    /**
     * Обработка завершена или нет
     */
    public boolean finished = false;

    /**
     * После завершения требуется очистка
     */
    public boolean clearable = false;

    /**
     * Принудительная передача данных в режиме many
     */
    public boolean many = false;

    /**
     * параметры в функцию
     */
    public Object[] params;

    /**
     * фильтрация
     */
    public Object[] filters;

    /**
     * Конструктор. По умолчанию указывается что разрешена отправка данных на сервер to = true
     * @param tableName имя таблицы
     */
    public Entity(String tableName){
        this(tableName, true, false);
    }

    public Entity(Class<?> aClass) {
        TableMetaData tableMetaData = ReflectionUtil.getTableMetaData(aClass);
        meta = tableMetaData;
        if(tableMetaData != null) {
            this.setSchema(tableMetaData.schema());
            useCFunction = tableMetaData.useMUIFunction();
            tableName = tableMetaData.name();
            to = tableMetaData.to();
            from = tableMetaData.from();
        }
    }

    /**
     * Конструктор
     * @param tableName имя таблицы
     * @param to разрешена отправка данных на сервер
     * @param from разрешена возможность получения данных с сервера
     */
    public Entity(String tableName, boolean to, boolean from) {
        this.tableName = tableName;
        this.to = to;
        this.from = from;
        this.tid = UUID.randomUUID().toString();
        category = "Общие";
    }

    /**
     * Создание сущности
     * @param tableName имя таблицы
     * @param to разрешена отправка данных на сервер
     * @param from разрешена возможность получения информации с сервера
     * @return Возвращается сущность
     */
    public static Entity createInstance(String tableName, boolean to, boolean from){
        return new Entity(tableName, to, from);
    }

    /**
     * Уставнваливается идентификатор для сущности
     * @param tid идентификатор
     * @return возвращается текущая сущность
     */
    public Entity setTid(String tid){
        this.tid = tid;
        return this;
    }

    public Entity setSchema(String schema) {
        this.schema = schema;
        return this;
    }

    /**
     * Устанавливается номер изменения для сущности
     * @param change номер изменения
     * @return возвращается текущая сущность
     */
    public Entity setChange(Double change) {
        this.change = change;
        return this;
    }

    /**
     * обработка сущности завершена
     * @return текущая сущность
     */
    public Entity setFinished() {
        this.finished = true;
        return this;
    }

    /**
     * После обработки требуется очистка
     * @return текущая сущность
     */
    public Entity setClearable() {
        this.clearable = true;
        return this;
    }

    /**
     * Принудительная передача данных в режиме many
     * @return текущая сущность
     */
    public Entity setMany() {
        this.many = true;
        return this;
    }

    /**
     * устновка списка колонок для выборки
     * @param select список
     * @return текущая сущность
     */
    public Entity setSelect(String ...select) {
        this.select = TextUtils.join(", ", select);
        return this;
    }

    /**
     * установить параметр useCFunction
     * @return текущий объект
     */
    public Entity setUseCFunction(){
        this.useCFunction = true;
        return this;
    }

    /**
     * параметры в RPC запрос
     * @param params параметры
     * @return текущий объект
     */
    public Entity setParam(Object... params) {
        this.params = params;
        return this;
    }

    /**
     * устновка фильтров
     * @param filters фильтры
     * @return текущий объект
     */
    public Entity setFilters(Object ...filters) {
        this.filters = filters;
        return this;
    }

    /**
     * устновка фильтра
     * @param filter фильтр
     * @return текущий объект
     */
    public Entity setFilter(FilterItem filter) {
        filters = new Object[1];
        filters[0] = filter;
        return this;
    }
}
