package com.mobwal.android.library.data.sync.util;

import com.mobwal.android.library.data.rpc.QueryData;
import com.mobwal.android.library.data.rpc.RPCItem;
import com.mobwal.android.library.data.rpc.SingleItemQuery;
import com.mobwal.android.library.data.sync.BaseSynchronization;

public class TableQuery {
    /**
     * имя таблицы для обработки
     */
    public String action;

    /**
     * псевдоним action
     */
    public String alias;

    /**
     * список полей которые требуется получить от сервера
     */
    public String select;
    /**
     * Версия состояния
     */
    public Double change;

    /**
     *
     * @param alias псевдоним
     */
    public TableQuery(String action, Double tableChange, String alias, String select) {
        this.action = action;
        this.alias = alias;
        this.select = select;
        this.change = tableChange;
    }

    /**
     * Создание объекта
     * @param action таблицы
     * @param select выборка полей
     * @return объект TableQuery
     */
    public TableQuery (String action, Double tableChange, String select) {
        this(action, tableChange, null, select);
    }

    /**
     * Создание объекта
     * @param action таблицы
     * @return объект TableQuery
     */
    public TableQuery (String action) {
        this(action, null, "");
    }

    /**
     * Преобразование в RPC запрос
     * @param limit лимит
     * @param filters фильтрация
     * @return RPC запрос
     */
    public RPCItem toRPCQuery(int limit, Object[] filters) {
        QueryData query = new QueryData();
        query.limit = limit;
        query.alias = alias;
        query.select = select;
        if(filters != null) {
            query.filter = filters;
        }

        RPCItem item = new RPCItem();
        item.action = action;
        item.method = "Query";
        item.data = new Object[1];
        item.data[0] = query;
        item.change = change;

        return item;
    }

    /**
     * Преобразование в RPC запрос
     * @param obj дополнительные параметры в функцию
     * @return RPC запрос
     */
    public RPCItem toRPCSelect(Object... obj) {
        RPCItem item = new RPCItem();
        item.action = action;
        item.method = "Select";
        item.data = new Object[1];
        item.data[0] = new SingleItemQuery(BaseSynchronization.MAX_COUNT_IN_QUERY, obj);
        item.change = change;

        return item;
    }
}
