package ru.mobnius.core.data.gallery;

import ru.mobnius.core.ui.image.ImageItem;

public interface OnPhotoItemChangeListener {
    /**
     * Сохранение результата
     * @param imageItem изображение
     */
    void onPhotoItemSave(ImageItem imageItem);
}
