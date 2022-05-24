package ru.mobnius.core.data.synchronization.utils;

/**
 * Обработчик пакетов в оба направления с удалением существующих записей
 */
public class LiteServerSidePackage extends FullServerSidePackage {
    public LiteServerSidePackage() {
        super();
        setDeleteRecordBeforeAppend(false);
    }
}
