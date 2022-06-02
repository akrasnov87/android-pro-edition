package com.mobwal.android.library.util;

import android.graphics.Bitmap;

import androidx.annotation.NonNull;
import androidx.collection.LruCache;

/**
 * Класс для кэширования изображений
 *
 * private static final BitmapCache sBitmapCache = new BitmapCache();
 *
 * Bitmap bitmap = ImageUtil.getSizedBitmap(bytes, 0, bytes.length, desiredWidth);
 * sBitmapCache.put(key, bitmap);
 *
 */
public class BitmapCache extends LruCache<String, Bitmap> {
    private static final int MAXIMUM_SIZE_IN_KB = 16 * 1024; // 16 Мб

    public BitmapCache() {
        super(getCacheSize());
    }

    @Override
    protected int sizeOf(@NonNull String key, @NonNull Bitmap value) {
        return value.getByteCount() / 1024;
    }

    private static int getCacheSize() {
        final int maxMemory = (int)(Runtime.getRuntime().maxMemory() / 1024);
        return Math.min(maxMemory / 8, MAXIMUM_SIZE_IN_KB);
    }
}
