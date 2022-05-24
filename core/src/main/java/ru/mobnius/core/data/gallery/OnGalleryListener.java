package ru.mobnius.core.data.gallery;

/**
 * Интерфейс для работы с галереей
 */
public interface OnGalleryListener {
    /**
     * Обработчик получения служебного объекта по работе с изображениями
     *
     * @return служебный объект по работе с изображением
     */
    BasePhotoManager getPhotoManager();

    /**
     * Обработчик вызова камеры
     */
    void onCamera();

    /**
     * Сохранение данных на форме
     */
    void onSaveFromGallery();

    /**
     * Удаление галереи
     */
    void onDestroyGallery();
}