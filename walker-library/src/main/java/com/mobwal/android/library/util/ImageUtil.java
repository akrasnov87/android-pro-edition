package com.mobwal.android.library.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;

import com.mobwal.android.library.Constants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.util.Date;

/**
 * Работа с изображениями
 */
public class ImageUtil {

    /**
     * Повернуть изображение
     * @param photoPath путь к изображению
     * @param bitmap изображения
     * @return скорректированное изображение, если не было ошибок
     */
    public static Bitmap normalRotateImage(@NonNull String photoPath, @NonNull Bitmap bitmap) {
        ExifInterface ei;
        try {
            ei = new ExifInterface(photoPath);
        } catch (IOException e) {
            Log.d(Constants.TAG, "Ошибка корректировки изображения (поворот). " + e);
            return bitmap;
        }

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

    /**
     * Повернуть изображение на определенный угол в градусах
     * @param source изображение
     * @param angle угол поворота
     * @return результат поворота
     */
    public static Bitmap getImageRotated(@NonNull Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }

    /**
     * Преобрзовать изображение в массив байтов
     * @param source изображение
     * @return массив байтов
     */
    public static byte[] bitmapToBytes(@NonNull Bitmap source) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        source.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        byte[] byteArray = stream.toByteArray();
        source.recycle();
        stream.close();
        return byteArray;
    }

    /**
     * сжатие изображения
     *
     * @param inputStream       поток
     * @param quality           качество сжатия от 0 до 100
     * @param MAX_IMAGE_HEIGHT  высота
     * @return сжатое изображение
     */
    @Nullable
    public static byte[] compress(@NonNull InputStream inputStream, int quality, int MAX_IMAGE_HEIGHT) {
        try {
            Bitmap bmp = BitmapFactory.decodeStream(inputStream);
            if (bmp == null) {
                return null;
            }

            Bitmap resizeBmp = scaleToFitHeight(bmp, MAX_IMAGE_HEIGHT);

            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            resizeBmp.compress(Bitmap.CompressFormat.JPEG, quality, bos);
            return bos.toByteArray();
        } catch (Exception e) {
            Log.d(Constants.TAG, "Ошибка в сжатии изображения", e);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                Log.d(Constants.TAG, "Ошибка при сжатии изображения. Ошибка закрытия потока.", e);
            }
        }

        return null;
    }

    /**
     * Пропорциональное изменение высота изображения
     * BitmapScale.scaleToFitHeight(bitmap, 100);
     *
     * @param b изображение
     * @param height высота в пикселях
     * @return скорректированное изображение
     */
    public static Bitmap scaleToFitHeight(@NonNull Bitmap b, int height) {
        float factor = height / (float) b.getHeight();
        return Bitmap.createScaledBitmap(b, (int) (b.getWidth() * factor), height, true);
    }

    /**
     * Размыть изображение
     *
     * @param context контекст
     * @param image изображение
     * @return размытое изображение
     */
    public static Bitmap blur(@NonNull Context context, @NonNull Bitmap image) {
        float BITMAP_SCALE = 0.4f;
        int width = Math.round(image.getWidth() * BITMAP_SCALE);
        int height = Math.round(image.getHeight() * BITMAP_SCALE);

        Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false);
        Bitmap outputBitmap = Bitmap.createBitmap(inputBitmap);

        RenderScript rs = RenderScript.create(context);
        ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap);
        Allocation tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
        float BLUR_RADIUS = 7.5f;
        theIntrinsic.setRadius(BLUR_RADIUS);
        theIntrinsic.setInput(tmpIn);
        theIntrinsic.forEach(tmpOut);
        tmpOut.copyTo(outputBitmap);

        return outputBitmap;
    }

    /**
     * Механизм быстрого сжатия изображения для вывода пользователю. Использовать только для кэширования
     * Bitmap bitmap = ImageUtil.getSizedBitmap(bytes, 0, bytes.length, desiredWidth);
     *
     * @param data массив байтов
     * @param offset индекс начала сжатия
     * @param length длина массива
     * @param desiredWidth ширина изображения в пикселях
     * @return изображение
     */
    public static Bitmap getSizedBitmap(@NonNull byte[] data, int offset, int length, int desiredWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(data, 0, data.length, options);

        options.inSampleSize = 1;
        options.inJustDecodeBounds = false;

        if(options.outWidth > desiredWidth) {
            final int halfWidth = options.outWidth / 2;
            while (halfWidth / options.inSampleSize > desiredWidth) {
                options.inSampleSize *= 2;
            }
        }

        return BitmapFactory.decodeByteArray(data, 0, data.length, options);
    }

    /**
     * Установка подписи на изображении
     * @param bitmap изображение
     * @param address адрес
     * @param coordinates координаты
     */
    public static Bitmap signBitmap(@NonNull Bitmap bitmap, @NonNull String address, @NonNull String coordinates, int fontSize) {
        Bitmap copyBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setTextSize(fontSize);
        Date date = new Date();

        String dateString = DateUtil.convertDateToUserString(date, DateUtil.SYSTEM_FORMAT);
        Canvas canvas = new Canvas(copyBitmap);
        canvas.drawText(dateString, 20, bitmap.getHeight() - 40, paint);
        if (!StringUtil.isEmptyOrNull(coordinates)) {
            canvas.drawText(coordinates, 20, bitmap.getHeight() - 40 - fontSize, paint);
        }
        if (!StringUtil.isEmptyOrNull(address)) {
            canvas.drawText(address, 20, bitmap.getHeight() - 40 - fontSize - fontSize, paint);
        }
        return copyBitmap;
    }
}
