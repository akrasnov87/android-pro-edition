package ru.mobnius.core.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ru.mobnius.core.R;
import ru.mobnius.core.adapter.holder.PhotoHolder;
import ru.mobnius.core.adapter.imageload.ImageLoadedCallback;
import ru.mobnius.core.adapter.imageload.ImageLoadingHelper;
import ru.mobnius.core.data.gallery.OnGalleryItemListener;
import ru.mobnius.core.ui.image.ImageItem;
import ru.mobnius.core.utils.StringUtil;

public class PhotoAdapter extends RecyclerView.Adapter<PhotoHolder> {

    private final OnGalleryItemListener mGalleryItemListener;
    @NonNull
    private final List<ImageItem> imageItems;

    public PhotoAdapter(final @NonNull Context context,
                        final @NonNull OnGalleryItemListener listener,
                        final @NonNull List<ImageItem> imageItems) {
        this.imageItems = imageItems;
        mGalleryItemListener = listener;
        final ImageLoadedCallback callback = new ImageLoadedCallback() {
            @Override
            public void onPhotoLoaded(@NonNull String imageId) {
                final ImageItem imageItem = getImageById(imageId);
                if (imageItem == null) {
                    return;
                }
                updateImage(imageItem);
            }

            @Override
            public void onErrorLoading(@NonNull String imageId) {
                final ImageItem imageItem = getImageById(imageId);
                if (imageItem == null) {
                    return;
                }
                imageItem.setErrorLoading(true);
                updateImage(imageItem);
            }
        };
        final Handler handler = new Handler(Looper.getMainLooper());
        final ImageLoadingHelper imageLoadingHelper = new ImageLoadingHelper(handler, imageItems, callback);
        imageLoadingHelper.startLoading(context);
    }

    @NonNull
    @Override
    public PhotoHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_point_photo, parent, false);
        return new PhotoHolder(view, mGalleryItemListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PhotoHolder holder, int position) {
        holder.bindPhoto(imageItems.get(position));
    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }

    public void addNewItem(final @NonNull ImageItem imageItem) {
        imageItems.add(imageItem);
        notifyItemInserted(imageItems.size() - 1);
    }

    public void removeImage(final @NonNull ImageItem imageItem) {
        int removePosition = -1;
        for (int i = 0; i < imageItems.size(); i++) {
            if (StringUtil.equalsIgnoreCase(imageItems.get(i).getId(), imageItem.getId())) {
                removePosition = i;
                break;
            }
        }
        if (removePosition < 0) {
            return;
        }
        imageItems.remove(removePosition);
        notifyItemRemoved(removePosition);
    }

    public void updateImage(final @NonNull ImageItem imageItem) {
        int updatePosition = -1;
        for (int i = 0; i < imageItems.size(); i++) {
            if (StringUtil.equalsIgnoreCase(imageItems.get(i).getId(), imageItem.getId())) {
                updatePosition = i;
                break;
            }
        }
        if (updatePosition < 0) {
            return;
        }
        imageItems.set(updatePosition, imageItem);
        notifyItemChanged(updatePosition);
    }

    @Nullable
    public ImageItem getImageById(final @NonNull String imageId) {
        for (final ImageItem imageItem : imageItems) {
            if (StringUtil.equalsIgnoreCase(imageItem.getId(), imageId)) {
                return imageItem;
            }
        }
        return null;
    }
}
