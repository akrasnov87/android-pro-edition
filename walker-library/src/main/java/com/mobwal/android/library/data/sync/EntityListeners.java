package com.mobwal.android.library.data.sync;

/**
 * интерфейс сущности
 */
public interface EntityListeners {
    /**
     * Уставнваливается идентификатор для сущности
     * @param tid идентификатор
     * @return возвращается текущая сущность
     */
    EntityListeners setTid(String tid);
}
