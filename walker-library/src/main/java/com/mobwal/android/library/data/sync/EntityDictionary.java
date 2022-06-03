package com.mobwal.android.library.data.sync;

/**
 * Справочник сущности
 */
public class EntityDictionary extends Entity{

    /**
     * Конструктор
     * @param tableName имя таблицы
     * @param to разрешена отправка данных на сервер
     * @param from разрешена возможность получения данных с сервера
     */
    public EntityDictionary(String tableName, boolean to, boolean from) {
        super(tableName, to, from);
        category = "Справочники";
        isDictionary = true;
    }
}
