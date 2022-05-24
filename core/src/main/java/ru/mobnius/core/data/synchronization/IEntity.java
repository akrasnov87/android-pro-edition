package ru.mobnius.core.data.synchronization;

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
