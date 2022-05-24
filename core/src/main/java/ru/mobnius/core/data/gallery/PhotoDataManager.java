package ru.mobnius.core.data.gallery;

import android.location.Location;

import androidx.annotation.NonNull;

import java.io.IOException;

import ru.mobnius.core.ui.image.ImageItem;

public interface PhotoDataManager {
    /**
     * обновление существующего файла
     * @param attachmentId идентификатор файла
     * @param type тип
     * @param notice описание
     */
    <T> T updateAttachment(String attachmentId, long type, String notice);
    /**
     * Удаление вложения
     * @param attachmentId идентификатор
     */
    void removeAttachment(String attachmentId);
    /**
     * сохранение файлов
     * @param c_name иям файла
     * @param fn_type тип файла, информация из таблицы cs_file_types
     * @param fn_point задание
     * @param fn_result результат выполнения задания
     * @param fn_route маршруты
     * @param notice примечание
     * @param location местоположение
     * @throws IOException исключение при работе с файловым менеджером
     */
    <T> T saveAttachment(String c_name, long fn_type, String fn_result, String fn_point, String fn_route, String notice, Location location, byte[] bytes) throws IOException;

    /**
     * Получение списка изображений привязанных к результату
     * @param resultId иден. результата, Results
     * @return список изображений, Image[]
     */
    ImageItem[] getImages(String resultId);
}
