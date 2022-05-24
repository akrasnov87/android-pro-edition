package ru.mobnius.core.data.gallery;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.ui.image.ImageItem;
import ru.mobnius.core.utils.StringUtil;

public abstract class BasePhotoManager implements Serializable {
    /**
     * Временные изображения, пока еще не хранятся в БД
     */
    protected List<ImageItem> mTempImages;

    /**
     * Изображения
     */
    protected List<ImageItem> mImages;

    protected OnGalleryChangeListeners mGalleryChange;

    private OnPhotoManagerProccessListeners mPhotoManagerProccessListeners;

    /**
     * конструктор
     */
    public BasePhotoManager(Context context) {
        mTempImages = new ArrayList<>(2);
        mImages = new ArrayList<>(2);

        if (context instanceof OnGalleryChangeListeners) {
            mGalleryChange = (OnGalleryChangeListeners) context;
        }
    }

    /**
     * Обновление изображения
     *
     * @param dataManager объект управления данными
     * @param image       Image
     * @param type        тип, AttachamentTypes
     * @param notice      примечание
     */
    public abstract void updatePicture(PhotoDataManager dataManager, ImageItem image, long type, String notice);

    /**
     * Удаление изображения
     *
     * @param dataManager объект управления данными
     * @param fileManager объект управления файлами
     * @param image       Image
     */
    public abstract void deletePicture(PhotoDataManager dataManager, FileManager fileManager, ImageItem image);

    /**
     * Сохранение изображений
     *
     * @param dataManager объект управления данными
     * @param fileManager объект управления файлами
     * @param routeId     идентификатор маршрута
     * @param resultId    идентификтаор результата
     */
    public abstract void savePictures(PhotoDataManager dataManager, FileManager fileManager, String routeId, String resultId) throws IOException, ParseException;

    /**
     * Можно ли обновлять вложение
     *
     * @param attachmentId иден. вложения
     * @return true - обновление разрешены
     */
    public abstract boolean isUpdateAttachment(String attachmentId);

    /**
     * Можно ли обновлять вложение
     *
     * @param attachmentId иден. вложения
     * @return true - обновление разрешены
     */
    public abstract boolean isLoadFromUrl(String attachmentId);

    /**
     * Доступны ли изображения в галереи
     *
     * @return true - изображения доступны
     */
    public abstract boolean isImagesExists();

    /**
     * Доступны ли изображения в галереи для точки
     *
     * @param resultId идентификатор результата
     * @return true - изображения доступны
     */
    public abstract boolean isImagesExists(String resultId);

    /**
     * Запись изображений по умолчанию
     *
     * @param images изображения, Image
     */
    public void addPictures(ImageItem[] images) {
        mImages.addAll(Arrays.asList(images));
    }

    /**
     * Обновление изображений по умолчанию
     *
     * @param images изображения, Image
     */
    public void updatePictures(ImageItem[] images) {
        mImages.clear();
        if (images != null) {
            mImages.addAll(Arrays.asList(images));
        }
    }

    /**
     * Добавить временного изображение
     *
     * @param image Image
     */
    public void addTempPicture(ImageItem image) throws Exception {
        boolean exists = findImage(image.getName()) != null;
        if (!exists) {
            mTempImages.add(image);
            onChange(OnGalleryChangeListeners.ADD, image);
        } else {
            throw new Exception("Изображение с именем " + image.getName() + " уже существует. ");
        }
    }

    /**
     * Получение списка изображение, отсортировано по дате
     *
     * @return список изображенией, Image
     */
    public ImageItem[] getImages() {

        List<ImageItem> images = new ArrayList<>(4);
        images.addAll(mTempImages);
        images.addAll(mImages);

        Collections.sort(images, new Comparator<ImageItem>() {
            @Override
            public int compare(ImageItem o1, ImageItem o2) {
                return Long.compare(o2.getDate().getTime(), o1.getDate().getTime());
            }
        });

        return images.toArray(new ImageItem[0]);
    }

    /**
     * Поиск изображения по имени
     *
     * @param name имя файла, Image.getName()
     * @return Результат поиска
     */
    public ImageItem findImage(String name) {
        ImageItem[] images = getImages();
        for (ImageItem image : images) {
            if (image.getName().equals(name)) {
                return image;
            }
        }
        return null;
    }

    @Nullable
    public ImageItem getImageById(final @NonNull String imageId){
        for (final ImageItem imageItem : getImages()) {
            if (StringUtil.equalsIgnoreCase(imageItem.getId(), imageId)){
                return imageItem;
            }
        }
        return null;
    }
    /**
     * Является ли изображение временным. Не было еще сохранено в БД
     *
     * @param image изображение, Image
     * @return true - является временным
     */
    public boolean isTempImage(final @NonNull ImageItem image) {
        for (ImageItem img : mTempImages) {
            if (image.getName().equals(img.getName())) {
                return true;
            }
        }
        return false;
    }

    /**
     * были изменения
     *
     * @return true - были изменения
     */
    public boolean isChanged() {
        boolean isChanged = false;
        for (ImageItem image : mImages) {
            isChanged = image.isChanged();
            if (isChanged) {
                break;
            }
        }
        return isChanged || mTempImages.size() > 0;
    }

    /**
     * Очистка данных
     *
     * @param fileManager объект управления файлами
     */
    public void destroy(FileManager fileManager) {
        clearTempImage(fileManager);
        mImages.clear();
    }

    public void clearTempImage(FileManager fileManager) {
        try {
            fileManager.deleteFolder(FileManager.TEMPORARY);
        } catch (FileNotFoundException ignored) {

        }
        mTempImages.clear();
    }

    protected void onChange(int type, ImageItem image) {
        if (mGalleryChange != null) {
            mGalleryChange.onGalleryChange(type, image);
        }
    }

    public OnPhotoManagerProccessListeners getPhotoManagerProccessListeners() {
        return mPhotoManagerProccessListeners;
    }

    public void setPhotoManagerProccessListeners(OnPhotoManagerProccessListeners photoManagerProccessListeners) {
        mPhotoManagerProccessListeners = photoManagerProccessListeners;
    }

    public interface OnPhotoManagerProccessListeners {
        void onPhotoManagerProccess(float f);

        void onPhotoManagerProccessStart();

        void onPhotoManagerProccessStop();
    }
}
