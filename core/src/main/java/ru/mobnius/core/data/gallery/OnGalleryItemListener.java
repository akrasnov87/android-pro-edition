package ru.mobnius.core.data.gallery;

import ru.mobnius.core.ui.image.ImageItem;

public interface OnGalleryItemListener {
    /**
     * Вывод изображения на карте
     * @param image изображение
     */
    void onImageMap(ImageItem image);

    /**
     * Окно изменения информации об изображении
     * @param image изображение
     */
    void onImageChangeDialog(ImageItem image);

    /**
     * Просмотр изображения на отдельном экране
     * @param image изображение
     */
    void onImageView(ImageItem image);
}
