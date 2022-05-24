package ru.mobnius.core.data.packager;

/**
 * Бинарный файл в пакете синхронизации
 */
public class FileBinary {
    public FileBinary(String name, String key, byte[] bytes) {
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
