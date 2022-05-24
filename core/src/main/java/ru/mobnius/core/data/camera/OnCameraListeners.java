package ru.mobnius.core.data.camera;

/**
 * интерфейс выполнения операции
 */
public interface OnCameraListeners {
    /**
     * Обработчик завершения
     * @param fileName имя файла
     * @param bytes массив байтов
     */
    void onCameraDone(String fileName, byte[] bytes, byte[] thumb);

    void onVideoCameraCompressStart();

    void onVideoCameraCompressProgress(float f);

    /**
     * Обработчик ошибок
     * @param e ошибка
     */
    void onCameraError(Exception e);
}
