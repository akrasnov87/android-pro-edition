package ru.mobnius.core.data.packager;

import com.google.gson.annotations.Expose;

/**
 * Вложенный файл
 */
public class MetaAttachment {
    /**
     * размер вложения
     */
    @Expose
    public final int size;

    /**
     * имя файла или относительный путь
     */
    @Expose
    public final String name;

    /**
     * ключ вложения
     */
    @Expose
    public final String key;

    /**
     * конструктор
     * @param size размер
     * @param name имя файла
     * @param key ключ
     */
    public MetaAttachment(int size, String name, String key) {
        this.size = size;
        this.key = key;
        this.name = name;
    }
}
