package com.mobwal.android.library.data.sync;

/**
 * Справочник сущности
 */
public class EntityAttachment extends Entity {

    /**
     * Конструктор
     * @param aClass сущность
     */
    public EntityAttachment(Class<?> aClass) {
        super(aClass);

        category = "Вложения";
    }
}
