package ru.mobnius.core.data.camera;

import android.app.Activity;
import android.net.Uri;

import java.io.File;

import ru.mobnius.core.NamesCore;

public class VideoManager {
    public static final int REQUEST_CODE_VIDEO = 145;
    private final Activity context;
    private String fileName;
    private String newFileName;
    //private VideoQuality quality;
    private File output;
    private Uri videoUri;
    private OnCameraListeners mListeners;
    /**
     * имя файла
     *
     * @return Возвращается имя файла
     */
    private String getFileName() {
        return newFileName;
    }

    public VideoManager(Activity context) {
        this.context = context;
    }

    public void open(String qualityStr) {
        /*VideoQuality quality;
        switch (qualityStr) {
            case "VERY_HIGH":
                quality = VideoQuality.VERY_HIGH;
                break;

            case "HIGH":
                quality = VideoQuality.HIGH;
                break;

            case "MEDIUM":
                quality = VideoQuality.MEDIUM;
                break;

            case "LOW":
                quality = VideoQuality.LOW;
                break;

            default:
            case "VERY_LOW":
                quality = VideoQuality.VERY_LOW;
                break;
        }

        this.quality = quality;
        fileName = System.currentTimeMillis() + Names.VIDEO_EXTENSION;

        File dir = FileManager.getInstance().getTempPictureFolder();
        if (!dir.exists()) {
            if(!dir.mkdirs()) {
                Logger.error(new Exception("Ошибка создания каталога"));
            }
        }
        output = new File(dir, fileName);
        videoUri = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".provider",
                output);
        Intent i = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        i.putExtra(MediaStore.EXTRA_OUTPUT, videoUri);
        i.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        // продолжительность видео
        i.putExtra(MediaStore.EXTRA_DURATION_LIMIT, PreferencesManager.getInstance().getVideoDuraction());
        i.putExtra(MediaStore.Video.Thumbnails.HEIGHT, 720);
        i.putExtra(MediaStore.Video.Thumbnails.WIDTH, 1280);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            i.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION |
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }

        if (i.resolveActivity(context.getPackageManager()) != null) {
            context.startActivityForResult(i, REQUEST_CODE_VIDEO);
        }*/
    }

    /**
     * Обработчик результата камеры
     *
     * @param resultCode статус код
     * @param callback   функция обратного вызова
     * @throws Exception исключение при сохранении изображения или обработке
     */
    public void processing(int resultCode, OnCameraListeners callback) throws Exception {
        mListeners = callback;
        if (resultCode == Activity.RESULT_OK) {
            newFileName = System.currentTimeMillis() + NamesCore.VIDEO_EXTENSION;
            final File newFile = new File(output.getParent() + "/" + newFileName);

            final Uri oneMoreUri = Uri.fromFile(newFile);
            /*VideoCompressor.start(output.getAbsolutePath(), newFile.getAbsolutePath(), new CompressionListener() {
                @Override
                public void onStart() {
                    mListeners.onVideoCameraCompressStart();
                }

                @Override
                public void onSuccess() {
                    // удаление оригинального файла
                    if (output.exists()) {
                        output.delete();
                    }
                    try {
                        InputStream iStream = context.getContentResolver().openInputStream(oneMoreUri);

                        // создаем миниатюрку
                        Bitmap bMap = ThumbnailUtils.createVideoThumbnail(newFile.getAbsolutePath(), MediaStore.Video.Thumbnails.MINI_KIND);
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bMap.compress(PreferencesManager.getInstance().getBitmapFormat(), BitmapUtil.IMAGE_QUALITY, stream);
                        byte[] byteArray = stream.toByteArray();
                        bMap.recycle();
                        FileManager fileManager = FileManager.getInstance();

                        fileManager.writeBytes(FileManager.PHOTOS, getFileName(), StreamUtil.readBytes(iStream));
                        mListeners.onCameraDone(getFileName(), byteArray, byteArray);
                    } catch (IOException e) {
                        String errorText = "Ошибка при сжатии изображения";
                        mListeners.onCameraError(new Exception(errorText + " " + getFileName() + ". " + e.getMessage()));
                    }
                }

                @Override
                public void onFailure(String failureMessage) {
                    mListeners.onCameraError(new Exception(failureMessage + " " + getFileName()));
                }

                @Override
                public void onProgress(float v) {
                    mListeners.onVideoCameraCompressProgress(v);
                }

                @Override
                public void onCancelled() {
                    mListeners.onCameraError(null);
                }
                //тут можно менять параметры влияющие на качество видео
            }, quality, false, false);
*/
        } else {
            throw new Exception("Вызов камеры был отменен. RequestCode=" + resultCode);
        }
    }
    public void destroy() {

    }
}
