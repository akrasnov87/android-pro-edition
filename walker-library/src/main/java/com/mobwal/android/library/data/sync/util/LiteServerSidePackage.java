package com.mobwal.android.library.data.sync.util;

/**
 * Обработчик пакетов в оба направления с удалением существующих записей
 */
public class LiteServerSidePackage extends FullServerSidePackage {
    public LiteServerSidePackage() {
        super();
        setDeleteRecordBeforeAppend(false);
    }
}
