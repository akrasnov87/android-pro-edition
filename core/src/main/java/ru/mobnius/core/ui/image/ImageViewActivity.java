package ru.mobnius.core.ui.image;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.StyledPlayerView;

import java.io.File;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import ru.mobnius.core.NamesCore;
import ru.mobnius.core.R;
import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.exception.IExceptionCode;
import ru.mobnius.core.ui.CoreActivity;
import ru.mobnius.core.utils.NetworkInfoUtil;
import ru.mobnius.core.utils.StringUtil;
import ru.mobnius.core.utils.TouchUtil;
import ru.mobnius.core.utils.UpdateUtil;

public class ImageViewActivity extends CoreActivity
        implements View.OnTouchListener, View.OnClickListener, UpdateUtil.OnDownloadProgressListener {

    public static final String IMAGE_ID = "ru.mobnius.core.ui.image.IMAGE_ID";
    public static final String IMAGE_DATE_TEXT = "ru.mobnius.core.ui.image.IMAGE_DATE_MILLIS";
    public static final String IMAGE_IS_VIDEO = "ru.mobnius.core.ui.image.IMAGE_IS_VIDEO";
    public static final String IMAGE_NAME = "ru.mobnius.core.ui.image.IMAGE_NAME";
    public static final String IMAGE_REMOTE_URL = "ru.mobnius.core.ui.image.IMAGE_REMOTE_URL";
    public static final String IMAGE_TYPE_NAME = "ru.mobnius.core.ui.image.IMAGE_TYPE_NAME";
    public static final int IMAGE_REMOVE_REQUEST_CODE = 9;
    public static final String IS_CAN_REMOVE_IMAGE = "is_can_remove";
    public static final String LOAD_FROM_URL = "load_from_url";

    private ImageView ivPhoto;
    private StyledPlayerView videoView;
    private ContentLoadingProgressBar pbLoadingVideo;
    @Nullable
    private ProgressBar pbImageLoading;

    private final Matrix matrix = new Matrix();
    private final Matrix savedMatrix = new Matrix();

    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;
    private int mode = NONE;
    private float oldDist = 1f;

    private final PointF start = new PointF();
    private final PointF mid = new PointF();

    private Bitmap mBitmap;
    @Nullable
    private String imageId;
    @Nullable
    private String imageName;
    @Nullable
    private String remoteUrl;

    private boolean isVideo;
    private boolean isCanImageRemove;
    private boolean isLoadFromUrl;

    public static Intent getIntent(Context context,
                                   final @NonNull String imageId,
                                   final @NonNull String imageName,
                                   final @NonNull String remoteUrl,
                                   final @NonNull String imageDate,
                                   final @NonNull String imageType,
                                   final boolean isVideo,
                                   boolean isCanImageRemove,
                                   boolean loadFromUrl) {
        Intent intent = new Intent(context, ImageViewActivity.class);
        intent.putExtra(IMAGE_ID, imageId);
        intent.putExtra(IMAGE_NAME, imageName);
        intent.putExtra(IMAGE_DATE_TEXT, imageDate);
        intent.putExtra(IMAGE_REMOTE_URL, remoteUrl);
        intent.putExtra(IMAGE_TYPE_NAME, imageType);
        intent.putExtra(IMAGE_IS_VIDEO, isVideo);
        intent.putExtra(ImageViewActivity.IS_CAN_REMOVE_IMAGE, isCanImageRemove);
        intent.putExtra(ImageViewActivity.LOAD_FROM_URL, loadFromUrl);

        return intent;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        isCanImageRemove = getIntent().getBooleanExtra(IS_CAN_REMOVE_IMAGE, false);
        isLoadFromUrl = getIntent().getBooleanExtra(LOAD_FROM_URL, false);

        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        ivPhoto = findViewById(R.id.image_view);
        ivPhoto.setOnTouchListener(this);

        pbLoadingVideo = findViewById(R.id.image_view_progress_bar);
        pbImageLoading = findViewById(R.id.image_view_loading_photo);
        videoView = findViewById(R.id.video_view);

        ImageButton btnRotate = findViewById(R.id.image_view_rotate);
        btnRotate.setOnClickListener(this);

        ImageButton btnCenter = findViewById(R.id.image_view_center);
        btnCenter.setOnClickListener(this);

        ImageButton btnDelete = findViewById(R.id.image_view_delete);
        btnDelete.setOnClickListener(this);
        if (getIntent() == null) {
            return;
        }
        imageId = StringUtil.defaultEmptyString(getIntent().getStringExtra(IMAGE_ID));
        imageName = StringUtil.defaultEmptyString(getIntent().getStringExtra(IMAGE_NAME));
        remoteUrl = StringUtil.defaultEmptyString(getIntent().getStringExtra(IMAGE_REMOTE_URL));
        final String imageDate = StringUtil.defaultEmptyString(getIntent().getStringExtra(IMAGE_DATE_TEXT));
        final String imageType = StringUtil.defaultEmptyString(getIntent().getStringExtra(IMAGE_TYPE_NAME));
        isVideo = getIntent().getBooleanExtra(IMAGE_IS_VIDEO, false);

        getSupportActionBar().setTitle(imageType);
        getSupportActionBar().setSubtitle(imageDate);
        if (!isCanImageRemove) {
            btnDelete.getBackground().setAlpha(30);
        } else {
            btnDelete.getBackground().setAlpha(255);
        }
        if (isVideo) {
            ivPhoto.setVisibility(View.GONE);

            btnCenter.setEnabled(false);
            btnCenter.getBackground().setAlpha(30);
            btnRotate.setEnabled(false);
            btnRotate.getBackground().setAlpha(30);
        } else {
            videoView.setVisibility(View.GONE);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!NetworkInfoUtil.isNetworkAvailable(this) || isCanImageRemove || !isLoadFromUrl) {
            if (isVideo) {
                SimpleExoPlayer player = new SimpleExoPlayer.Builder(this).build();
                videoView.setPlayer(player);
                videoView.setShowFastForwardButton(false);
                videoView.setShowNextButton(false);
                videoView.setShowPreviousButton(false);
                videoView.setShowRewindButton(false);
                videoView.setBackgroundColor(getResources().getColor(R.color.colorSecondaryText));
                File dir = FileManager.getInstance().getTempPictureFolder();
                if (imageName == null || imageName.isEmpty()) {
                    return;
                }
                File image = new File(dir, imageName.replace("." + PreferencesManager.getInstance().getImageFormat(), NamesCore.VIDEO_EXTENSION));
                if (!image.exists()) {
                    dir = FileManager.getInstance().getAttachmentsFolder();
                    image = new File(dir, imageName.replace("." + PreferencesManager.getInstance().getImageFormat(), NamesCore.VIDEO_EXTENSION));
                }
                MediaItem mediaItem = MediaItem.fromUri(image.getAbsolutePath());
                player.setMediaItem(mediaItem);
                player.prepare();
                videoView.showController();
            } else {
                ViewTreeObserver viewTreeObserver = ivPhoto.getViewTreeObserver();
                viewTreeObserver.addOnGlobalLayoutListener(() -> {
                    if (imageName == null) {
                        return;
                    }
                    File dir = FileManager.getInstance().getTempPictureFolder();
                    File image = new File(dir, imageName);
                    BitmapFactory.Options bmOptions = new BitmapFactory.Options();
                    mBitmap = BitmapFactory.decodeFile(image.getAbsolutePath(), bmOptions);

                    if (mBitmap == null && getBytes() != null) {
                        byte[] bytes = getBytes();
                        mBitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    }
                    if (mBitmap != null) {

                        centerImage(mBitmap);

                        ivPhoto.setImageMatrix(matrix);
                        ivPhoto.setImageBitmap(mBitmap);
                    }
                });
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (videoView != null && videoView.getPlayer() != null) {
            videoView.getPlayer().stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (NetworkInfoUtil.isNetworkAvailable(this) && !isCanImageRemove && isLoadFromUrl) {
            if (isVideo) {
                pbLoadingVideo.setVisibility(View.VISIBLE);
                if (StringUtil.isEmptyOrNull(remoteUrl) || StringUtil.isEmptyOrNull(imageName)) {
                    return;
                }
                setErrorMessage("Идет загрузка...");
                UpdateUtil.getVideoFile(imageName, remoteUrl, new Handler(getMainLooper()), this);
            } else {
                if (pbImageLoading != null) {
                    pbImageLoading.setVisibility(View.VISIBLE);
                }
                final Executor executor = Executors.newSingleThreadExecutor();
                executor.execute(() -> {
                    String error = "";
                    try {
                        mBitmap = Glide.with(this).asBitmap().load(remoteUrl).submit().get(3, TimeUnit.SECONDS);
                    } catch (ExecutionException | InterruptedException | TimeoutException e) {
                        e.printStackTrace();
                        error = StringUtil.defaultEmptyString(e.getMessage());
                    }
                    if (mBitmap == null) {
                        final String finalError = error;
                        runOnUiThread(() -> {
                            setErrorMessage("Не удалось получить файл\n" + finalError);
                            if (pbImageLoading != null) {
                                pbImageLoading.setVisibility(View.GONE);
                            }
                        });
                        return;
                    }
                    runOnUiThread(() -> {
                        if (pbImageLoading != null) {
                            pbImageLoading.setVisibility(View.GONE);
                        }
                        ivPhoto.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                centerImage(mBitmap);
                                ivPhoto.setImageMatrix(matrix);
                                ivPhoto.setImageBitmap(mBitmap);
                                ivPhoto.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            }
                        });

                    });
                });
            }
        } else {
            if (!NetworkInfoUtil.isNetworkAvailable(this) && !isCanImageRemove) {
                setErrorMessage(getResources().getString(R.string.need_internet_for_original_photo));
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        ImageView view = (ImageView) v;
        view.setScaleType(ImageView.ScaleType.MATRIX);
        float scale;

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:   // палец на экране
                savedMatrix.set(matrix);
                start.set(event.getX(), event.getY());
                mode = DRAG;
                break;

            case MotionEvent.ACTION_UP: // палец убран с экрана

            case MotionEvent.ACTION_POINTER_UP: // второй палец убран с экрана

                mode = NONE;
                break;

            case MotionEvent.ACTION_POINTER_DOWN: // оба пальца на экране

                oldDist = TouchUtil.spaceCalculation(event);
                if (oldDist > 5f) {
                    savedMatrix.set(matrix);
                    TouchUtil.midPoint(mid, event);
                    mode = ZOOM;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                Log.d("pu", " " + event.getY());
                int displayHeight = getWindow().getDecorView().getHeight() - 500;
                if (mode == DRAG && event.getY() > 0 && event.getY() < displayHeight) {
                    matrix.set(savedMatrix);
                    matrix.postTranslate(event.getX() - start.x, event.getY() - start.y);
                } else if (mode == ZOOM) {

                    float newDist = TouchUtil.spaceCalculation(event);
                    if (newDist > 5f) {
                        matrix.set(savedMatrix);
                        scale = newDist / oldDist;
                        matrix.postScale(scale, scale, mid.x, mid.y);
                    }
                }
                break;
        }

        view.setImageMatrix(matrix); // отображение изменений на экране

        return true;

    }

    @Override
    public void onClick(View v) {
        float centerVertical = (float) ivPhoto.getHeight() / 2;
        float centerHorizontal = (float) ivPhoto.getWidth() / 2;

        int id = v.getId();
        if (id == R.id.image_view_delete) {// если разрешено удаление
            if (isCanImageRemove) {
                confirmDialog((dialog, which) -> {
                    Intent data = new Intent();
                    data.putExtra(IMAGE_ID, imageId);
                    setResult(RESULT_OK, data);
                    finish();
                });
            }
        } else if (id == R.id.image_view_center) {
            centerImage(mBitmap);
            ivPhoto.setImageMatrix(matrix);
            ivPhoto.setImageBitmap(mBitmap);
        } else if (id == R.id.image_view_rotate) {
            matrix.postRotate(90.0F, centerHorizontal, centerVertical);
            ivPhoto.setImageMatrix(matrix);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    private void centerImage(Bitmap bitmap) {
        if (bitmap == null) {
            return;
        }
        float width = (float) bitmap.getWidth();
        float displayWidth = (float) (getWindow()).getDecorView().getWidth();
        float index = displayWidth / width;
        float offsetX = (ivPhoto.getWidth() - bitmap.getWidth()) / 2F;
        float offsetY = (ivPhoto.getHeight() - bitmap.getHeight()) / 2F;

        float centerX = ivPhoto.getWidth() / 2F;
        float centerY = ivPhoto.getHeight() / 2F;

        matrix.setScale(index, index, centerX, centerY);
        matrix.preTranslate(offsetX, offsetY);
    }

    @Override
    public int getExceptionCode() {
        return IExceptionCode.IMAGE_VIEW;
    }

    private void confirmDialog(DialogInterface.OnClickListener listener) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setPositiveButton(getResources().getString(R.string.yes), listener);

        adb.setNegativeButton(getResources().getString(R.string.exit), null);

        AlertDialog alert = adb.create();
        alert.setTitle("Вы уверены что хотите удалить " + (isVideo ? "видео" : "изображение") + "?");
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onProgress(int progress) {
        pbLoadingVideo.setProgress(progress);
    }

    @Override
    public void onErrorProgress(String message) {
        setErrorMessage(message);
        pbLoadingVideo.setVisibility(View.GONE);
    }

    @Override
    public void onFinishDownload(File video) {
        getErrorMessage().setVisibility(View.GONE);
        pbLoadingVideo.setVisibility(View.GONE);
        SimpleExoPlayer player = new SimpleExoPlayer.Builder(this).build();
        videoView.setPlayer(player);
        videoView.setShowFastForwardButton(false);
        videoView.setShowNextButton(false);
        videoView.setShowPreviousButton(false);
        videoView.setShowRewindButton(false);
        videoView.setBackgroundColor(getResources().getColor(R.color.colorSecondaryText));
        player.prepare();
        player.setMediaItem(MediaItem.fromUri(Uri.fromFile(video)));
        videoView.showController();
    }

    @Nullable
    public byte[] getBytes() {
        if (StringUtil.isEmptyOrNull(imageName)) {
            return null;
        }
        try {
            return FileManager.getInstance().readPath(FileManager.PHOTOS, imageName);
        } catch (IOException ignored) {
            return null;
        }
    }
}
