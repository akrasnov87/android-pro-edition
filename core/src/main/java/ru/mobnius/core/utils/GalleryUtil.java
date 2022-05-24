package ru.mobnius.core.utils;

import android.content.Intent;

import ru.mobnius.core.data.gallery.BasePhotoManager;

public class GalleryUtil {
    private final static String MANAGER = "manager";

    public static BasePhotoManager deSerializable(Intent intent) {
        if(intent.hasExtra(MANAGER)) {
            return (BasePhotoManager) intent.getSerializableExtra(MANAGER);
        } else {
            return null;
        }
    }

    public static void serializable(Intent intent, BasePhotoManager photoManager) {
        intent.putExtra(MANAGER, photoManager);
    }
}