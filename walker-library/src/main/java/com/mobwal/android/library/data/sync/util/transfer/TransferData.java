package com.mobwal.android.library.data.sync.util.transfer;

import android.content.Context;
import androidx.annotation.NonNull;

import com.mobwal.android.library.util.StringUtil;

/**
 * Данные
 */
public class TransferData {
    /**
     * Текущая позиция
     */
    private final int mPosition;
    /**
     * общий размер
     */
    private final int mTotal;

    /**
     * Создани экземпляра объекта
     * @param position текущай позиция
     * @param total общий размер данных
     * @return Данные
     */
    public TransferData(int position, int total) {
        this.mPosition = position;
        this.mTotal = total;
    }

    public String toString(@NonNull Context context) {
        return StringUtil.getSize(context, mPosition) + "/" +StringUtil.getSize(context, mTotal);
    }
}
