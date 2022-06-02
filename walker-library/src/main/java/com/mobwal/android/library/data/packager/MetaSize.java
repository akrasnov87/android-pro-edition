package com.mobwal.android.library.data.packager;


import androidx.annotation.NonNull;

/**
 * информация о чтении мета информации
 */
public class MetaSize {
    /**
     * максимальная длина строки
     */
    static final int MAX_LENGTH = 16;
    /**
     * разделитель для заполнения
     */
    private static final String separator = ".";

    /**
     * пакет создан
     */
    public static final int CREATED = 0;
    /**
     * пакет доставлен
     */
    public static final int DELIVERED = 1;
    /**
     * в обработке
     */
    public static final int PROCESSING = 2;
    /**
     * обработан
     */
    public static final int PROCESSED = 3;
    /**
     * Не доставлен
     */
    public static final int UN_DELIVERED = 8;
    /**
     * обработан с ошибкой
     */
    public static final int PROCESSED_ERROR = 9;

    /**
     * Длина метаинформации
     */
    public int metaSize;

    /**
     * Статус обработки пакета
     */
    public int status;

    /**
     * Тип пакета
     */
    public String type;

    public MetaSize(int metaSize, int status, @NonNull String type) {
        this.metaSize = metaSize;
        this.status = status;
        this.type = type;
    }

    public String toJsonString() {
        StringBuilder result = new StringBuilder(type);
        result.append(metaSize);
        for(int i = 0; i < MAX_LENGTH -1; i++) {
            if(i >= result.length()){
                result.append(separator);
            }
        }
        result.append(status);
        return result.toString();
    }
}
