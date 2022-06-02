package com.mobwal.pro.data;

/**
 * Справочник сущности
 */
public class EntityAttachment extends Entity {

    /**
     * Конструктор
     * @param tableName имя таблицы
     * @param to разрешена отправка данных на сервер
     * @param from разрешена возможность получения данных с сервера
     */
    public EntityAttachment(String tableName, boolean to, boolean from) {
        super(tableName, to, from);
        category = "Вложения";
    }
}
