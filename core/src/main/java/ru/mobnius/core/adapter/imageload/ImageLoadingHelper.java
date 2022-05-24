package ru.mobnius.core.adapter.imageload;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.ui.image.ImageItem;

public class ImageLoadingHelper {
    @NonNull
    private final Handler handler;
    @NonNull
    private final List<ImageItem> imageItems;
    @NonNull
    private final ImageLoadedCallback imageLoadedCallback;
    @Nullable
    private Executor executor;

    public ImageLoadingHelper(final @NonNull Handler handler,
                              final @NonNull List<ImageItem> imageItems,
                              final @NonNull ImageLoadedCallback imageLoadedCallback) {
        this.handler = handler;
        this.imageItems = imageItems;
        this.imageLoadedCallback = imageLoadedCallback;
        executor = Executors.newSingleThreadExecutor();
    }

    public void startLoading(final @NonNull Context context) {
        if (executor != null) {
            executor = null;
        }
        executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            for (final ImageItem imageItem : imageItems) {
                try {
                    if (imageItem.getThumbs() != null) {
                        continue;
                    }
                    final Bitmap bitmap = Glide.with(context).asBitmap().load(imageItem.getRemoteUrl(false)).submit().get(3, TimeUnit.SECONDS);
                    if (bitmap == null) {
                        handler.post(() -> imageLoadedCallback.onErrorLoading(imageItem.getId()));
                    } else {
                        final File dir = FileManager.getInstance().getTemporaryFolder();
                        if (dir == null){
                            handler.post(() -> imageLoadedCallback.onErrorLoading(imageItem.getId()));
                            return;
                        }
                        if (!dir.exists()){
                            if(!dir.mkdirs()){
                                handler.post(() -> imageLoadedCallback.onErrorLoading(imageItem.getId()));
                                return;
                            }
                        }
                        final File fileImage = new File(dir, imageItem.getName());
                        try (FileOutputStream out = new FileOutputStream(fileImage)) {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                        } catch (IOException e) {
                            e.printStackTrace();
                            handler.post(() -> imageLoadedCallback.onErrorLoading(imageItem.getId()));
                            return;
                        }
                        handler.post(() -> imageLoadedCallback.onPhotoLoaded(imageItem.getId()));

                    }
                } catch (ExecutionException | InterruptedException | TimeoutException e) {
                    handler.post(() -> imageLoadedCallback.onErrorLoading(imageItem.getId()));
                }
            }
        });
    }

    public void destroy() {
        executor = null;
    }

}
