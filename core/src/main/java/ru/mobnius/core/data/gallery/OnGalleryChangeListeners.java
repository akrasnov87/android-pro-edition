package ru.mobnius.core.data.gallery;

import ru.mobnius.core.ui.image.ImageItem;

public interface OnGalleryChangeListeners {
    int ADD = 0;
    int UPDATE = 1;
    int REMOVE = 2;

    void onGalleryChange(int type, ImageItem image);
}
