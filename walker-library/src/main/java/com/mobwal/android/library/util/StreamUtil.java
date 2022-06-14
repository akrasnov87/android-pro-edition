package com.mobwal.android.library.util;

import androidx.annotation.NonNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

public class StreamUtil {

    /**
     * Чтение из потока
     *
     * @param inputStream входной поток
     * @return массив байтов
     */
    public static byte[] readBytes(@NonNull InputStream inputStream) throws IOException {
        // this dynamically extends to take the bytes you read
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();

        // this is storage overwritten on each iteration with bytes
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        // we need to know how may bytes were read to write them to the byteBuffer
        int len;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }

        // and then we can return your byte array.
        byte[] bytes = byteBuffer.toByteArray();
        byteBuffer.close();
        inputStream.close();
        return bytes;
    }

    /**
     * Загрузка изображения с сервера
     * @param src путь к изображению
     * @return объект Bitmap
     */
    public static byte[] readURL(@NonNull String src, int timeout) throws IOException {
        java.net.URL url = new java.net.URL(src);
        HttpURLConnection connection = (HttpURLConnection) url
                .openConnection();
        connection.setDoInput(true);
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        connection.connect();
        InputStream input = connection.getInputStream();
        return StreamUtil.readBytes(input);
    }
}
