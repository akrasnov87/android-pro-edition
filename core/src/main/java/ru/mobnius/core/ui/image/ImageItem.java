package ru.mobnius.core.ui.image;

import android.graphics.Bitmap;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.Serializable;
import java.util.Date;

public interface ImageItem extends Serializable {
    /**
     * Тип изображения
     */
    long getType();

    /**
     * Наименование типа изображения
     */
    String getTypeName();

    /**
     * Массив байтов
     */
    @Nullable
    byte[] getBytes();

    /**
     * Получение ссылки на удаленное изображение
     *
     * @param isPreview получить превью (миниатюрку)
     */
    String getRemoteUrl(boolean isPreview);

    Date getDate();

    String getName();

    boolean isVideo();

    boolean isChanged();

    String getId();

    byte[] getThumbs();

    Location getLocation();

    String getNotice();

    void setType(long type);

    void setNotice(String notice);

    String getResultTypeName();

    String getResultId();

    boolean isErrorLoading();

    void setErrorLoading(boolean errorLoading);
}
