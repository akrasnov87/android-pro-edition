package com.mobwal.pro.data.utils.transfer;

import androidx.annotation.NonNull;

import ru.mobnius.core.utils.StringUtil;

/**
 * Данные
 */
public class TransferData {
    /**
     * Текущая позиция
     */
    private int position;
    /**
     * общий размер
     */
    private int total;

    /**
     * Создани экземпляра объекта
     * @param position текущай позиция
     * @param total общий размер данных
     * @return Данные
     */
    public static TransferData getInstance(int position, int total){
        TransferData data = new TransferData();
        data.position = position;
        data.total = total;

        return data;
    }

    @NonNull
    @Override
    public String toString() {
        return StringUtil.getSize(position) + "/" +StringUtil.getSize(total);
    }
}
