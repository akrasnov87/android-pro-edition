package com.mobwal.android.library.data.sync.util.transfer;

import android.content.Context;

import androidx.annotation.NonNull;

import com.mobwal.android.library.util.StringUtil;

/**
 * Скорость передачи данных. Блоков chunk за время time
 */
public class TransferSpeed {

    /**
     * размер блока
     */
    private final long mChunk;

    /**
     * время затраченное на обработку
     */
    private final long mTime;

    public TransferSpeed(long chunk, long time) {
        this.mChunk = chunk;
        this.mTime = time == 0 ? 1 : time;
    }

    public String toString(@NonNull Context context) {
        return StringUtil.getSize(context, (1000 * mChunk) / mTime) + "\\сек.";
    }
}
