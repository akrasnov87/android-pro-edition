package com.mobwal.pro.data;

/**
 * интерфейс сущности
 */
public interface IEntity {
    /**
     * Уставнваливается идентификатор для сущности
     * @param tid идентификатор
     * @return возвращается текущая сущность
     */
    IEntity setTid(String tid);
}
