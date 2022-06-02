package ru.mobnius.core.data.camera;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import ru.mobnius.core.data.FileManager;
import com.mobwal.android.library.authorization.Authorization;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.logger.Logger;

/**
 * класс для управления камерой в заданиях
 */
public class CameraManager {
    public static final int REQUEST_CODE_PHOTO = 2;

    private CompressAsync compressAsync;
    private OnCameraListeners mListeners;

    private final Activity context;
    /**
     * Качество сохраняемого изображения
     */
    private int quality;
    private String fileName;
    private Uri imageUri;
    File output;

    /**
     * имя файла
     *
     * @return Возвращается имя файла
     */
    private String getFileName() {
        return fileName;
    }

    /**
     * Конструктор
     *
     * @param context Текущее активити
     */
    public CameraManager(Activity context) {
        this.context = context;
    }

    /**
     * Вызыв камеры
     *
     * @param quality качество камеры от 0 до 100, где 100 - это максимальное качество
     */
    public void open(int quality) {
        this.quality = quality;
        fileName = System.currentTimeMillis() + ".jpg";
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File dir = FileManager.getInstance().getTempPictureFolder();
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Logger.error(new Exception("Ошибка создания каталога"));
            }
        }
        output = new File(dir, fileName);
        imageUri = FileProvider.getUriForFile(
                context,
                context.getPackageName() + ".provider",
                output);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION
                | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION);
        context.startActivityForResult(intent, REQUEST_CODE_PHOTO);
    }

    /**
     * Обработчик результата камеры
     *
     * @param resultCode статус код
     * @param callback   функция обратного вызова
     * @throws Exception исключение при сохранении изображения или обработке
     */
    public void processing(String coordinates, String address, int resultCode, OnCameraListeners callback) throws Exception {
        mListeners = callback;
        if (resultCode == Activity.RESULT_OK) {
            InputStream iStream = context.getContentResolver().openInputStream(imageUri);

            compressAsync = new CompressAsync(address, coordinates);
            compressAsync.execute(iStream);

        } else {
            throw new Exception("Вызов камеры был отменен. RequestCode=" + resultCode);
        }
    }

    public void processing(int resultCode, OnCameraListeners callback) throws Exception {
        processing(null, null, resultCode, callback);
    }

    public void destroy() {
        if (compressAsync != null) {
            if (!compressAsync.isCancelled()) {
                compressAsync.cancel(true);
            }
            compressAsync = null;
        }
    }

    @SuppressLint("StaticFieldLeak")
    class CompressAsync extends AsyncTask<InputStream, Void, byte[]> {

        /**
         * событие о том что произошла ошибка
         */
        private boolean isMistake = false;
        private final String address;
        private final String coordinates;

        public CompressAsync(String address, String coordinates) {
            this.address = address;
            this.coordinates = coordinates;
        }

        @Override
        protected byte[] doInBackground(InputStream... inputStreams) {
            byte[] bytes = CameraUtil.compress(inputStreams[0], PreferencesManager.getInstance().getImageFormat(), quality, address, coordinates, output.getAbsolutePath(), context);
            isMistake = bytes == null;
            return bytes;
        }

        @Override
        protected void onPostExecute(byte[] bytes) {
            super.onPostExecute(bytes);

            if (mListeners != null) {
                String fileName = null;
                String errorText = "Ошибка при сжатии изображения";
                if (isMistake) {
                    mListeners.onCameraError(new Exception(errorText + " " + getFileName()));
                } else {
                    try {
                        FileManager fileManager = FileManager.getInstance();
                        fileName = getFileName();
                        CameraUtil.saveDataFromCamera(fileManager, fileName, bytes);
                    } catch (IOException e) {
                        mListeners.onCameraError(new Exception(errorText + " " + getFileName() + ". " + e.getMessage()));
                    }
                }
                mListeners.onCameraDone(fileName, bytes, null);
            }
        }
    }

}
