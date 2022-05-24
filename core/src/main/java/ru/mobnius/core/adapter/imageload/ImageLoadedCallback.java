package ru.mobnius.core.adapter.imageload;


import androidx.annotation.NonNull;

public interface ImageLoadedCallback {

    void onPhotoLoaded(final @NonNull String imageId);

    void onErrorLoading(final @NonNull String imageId);
}
