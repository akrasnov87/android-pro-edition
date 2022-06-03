package com.mobwal.android.library.data.sync.util;

/**
 * Обработчик пакетов в оба направления с удалением существующих записей
 */
public class FullServerSidePackage extends ServerSidePackage {
    public FullServerSidePackage() {
        setDeleteRecordBeforeAppend(true);
    }
}
