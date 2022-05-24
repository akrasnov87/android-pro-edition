package ru.mobnius.core.data.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.ExifInterface;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

import ru.mobnius.core.R;
import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.utils.BitmapUtil;
import ru.mobnius.core.utils.StringUtil;

import static ru.mobnius.core.utils.DateUtil.convertDateToUserString;

public class CameraUtil {

    public static final String WEBP_IMAGE_FORMAT = "webp";
    public static final String JPEG_IMAGE_FORMAT = "jpeg";
    /**
     * Максимальная высота изображения для сохранения в БД
     */
    public static int MAX_IMAGE_HEIGHT = BitmapUtil.QUALITY_1080p;

    /**
     * сжатие изображения
     *
     * @param inputStream поток
     * @param imageFormat формат изображения WEBP_IMAGE_FORMAT или JPEG_IMAGE_FORMAT
     * @param quality     качество сжатия от о до 100
     * @return сжатие данные
     */
    public static byte[] compress(InputStream inputStream, String imageFormat, int quality, String address, String coordinates, String filePath, Context context) {
        try {
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            if (bmp == null) {
                return null;
            }
            Bitmap resizeBmp;
            if (StringUtil.isEmptyOrNull(address) && StringUtil.isEmptyOrNull(coordinates)) {
                resizeBmp = BitmapUtil.scaleToFitHeight(bmp, MAX_IMAGE_HEIGHT);
            } else {
                if (StringUtil.isEmptyOrNull(coordinates)) {
                    coordinates = "";
                }
                if (StringUtil.isEmptyOrNull(address)) {
                    address = "";
                }
                Bitmap signed = signBitmap(bmp, address, coordinates, filePath, context);
                resizeBmp = BitmapUtil.scaleToFitHeight(signed, MAX_IMAGE_HEIGHT);
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();

            switch (imageFormat) {
                default:
                case JPEG_IMAGE_FORMAT:
                    resizeBmp.compress(Bitmap.CompressFormat.JPEG, quality, bos);
                    break;

                case WEBP_IMAGE_FORMAT:
                    resizeBmp.compress(Bitmap.CompressFormat.WEBP, quality, bos);
                    break;
            }
            return bos.toByteArray();
        } catch (Exception e) {
            Logger.error(e);
            return null;
        }finally {
            if (inputStream!=null){
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * сохранение результата после фотографирования
     *
     * @param fileManager менеджер файлов
     * @param fileName    имя файла
     * @param bytes       данные
     * @throws IOException исключение
     */
    public static void saveDataFromCamera(FileManager fileManager, String fileName, byte[] bytes) throws IOException {
        fileManager.writeBytes(FileManager.PHOTOS, fileName, bytes);
    }

    public static Bitmap signBitmap(Bitmap background, String address, String coordinates, String filename, Context context) throws IOException {
        Bitmap rotatedBackground = rotateImage(filename, background);
        rotatedBackground = rotatedBackground.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        int fontSize = context.getResources().getDimensionPixelSize(R.dimen.very_high_font_size);
        paint.setTextSize(fontSize);
        Date date = new Date();
        String dateString = convertDateToUserString(date, "dd.MM.yyyy HH:mm:ss");
        Canvas canvas = new Canvas(rotatedBackground);
        canvas.drawText(dateString, 100, rotatedBackground.getHeight() - 40, paint);
        if (!StringUtil.isEmptyOrNull(coordinates)) {
            canvas.drawText(coordinates, 100, rotatedBackground.getHeight() - 40 - fontSize, paint);
        }
        if (!StringUtil.isEmptyOrNull(address)) {
            canvas.drawText(address, 100, rotatedBackground.getHeight() - 40 - fontSize - fontSize, paint);
        }
        return rotatedBackground;
    }

    public static Bitmap rotateImage(String photoPath, Bitmap bitmap) throws IOException {
        ExifInterface ei = new ExifInterface(photoPath);
        int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED);
        Bitmap rotatedBitmap;


        switch (orientation) {

            case ExifInterface.ORIENTATION_ROTATE_90:
                rotatedBitmap = getImageRotated(bitmap, 90);
                break;

            case ExifInterface.ORIENTATION_ROTATE_180:
                rotatedBitmap = getImageRotated(bitmap, 180);
                break;

            case ExifInterface.ORIENTATION_ROTATE_270:
                rotatedBitmap = getImageRotated(bitmap, 270);
                break;

            case ExifInterface.ORIENTATION_NORMAL:
            default:
                rotatedBitmap = bitmap;
        }
        return rotatedBitmap;
    }

    private static Bitmap getImageRotated(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        RectF rect = new RectF(0, 0, source.getWidth(), source.getHeight());
        Bitmap targetBitmap = Bitmap.createBitmap((int) rect.height(), (int) rect.width(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(targetBitmap);
        matrix.setRotate(angle, (float) targetBitmap.getWidth() / 2, (float) source.getHeight() / 2);
        canvas.drawBitmap(source, matrix, new Paint());
        return targetBitmap;
    }

}
