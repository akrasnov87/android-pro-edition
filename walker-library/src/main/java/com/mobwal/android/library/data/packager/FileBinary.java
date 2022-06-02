package com.mobwal.android.library.data.packager;

import androidx.annotation.NonNull;

/**
 * Бинарный файл в пакете синхронизации
 */
public class FileBinary {
    public FileBinary(@NonNull String name, @NonNull String key, @NonNull byte[] bytes) {
        this.name = name;
        this.key = key;
        this.bytes = bytes;
    }

    /**
     * имя файла
     */
    public String name;

    /**
     * ключ файла
     */
    public String key;

    /**
     * Массив байтов
     */
    public byte[] bytes;
}
