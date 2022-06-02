package com.mobwal.android.library;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.mobwal.android.library.util.LogUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;

/**
 * Простой файловый менеджер
 */
public class SimpleFileManager {
    private static final int BUFFER_SIZE = 2048;

    private final File mEnvironment;
    private final Context mContext;

    /**
     * Хранение данных
     *
     * @param context контекст
     * @param environment директория из context.getFileDir() | context.getCacheDir()
     */
    public SimpleFileManager(@NonNull Context context, @NonNull File environment) {
        mContext = context;
        mEnvironment = environment;
    }

    /**
     * Возвращается корневой каталог
     * @param folder основной каталог
     * @return объект File
     */
    public File getRootCatalog(@NonNull String folder) {
        return TextUtils.isEmpty(folder) ? mEnvironment : new File(mEnvironment, folder);
    }

    /**
     * Запись байтов в файловую систему
     *
     * @param folder   папка
     * @param fileName имя файла
     * @param bytes    массив байтов
     * @throws IOException исключение
     */
    public void writeBytes(@NonNull String folder, @NonNull String fileName, @NonNull byte[] bytes) throws IOException {
        File dir = getRootCatalog(folder);

        writeBytes(dir, fileName, bytes);
    }

    /**
     * Запись байтов в файловую систему
     *
     * @param folder   папка
     * @param fileName имя файла
     * @param bytes    массив байтов
     * @throws IOException исключение
     */
    public void writeBytes(@NonNull File folder, @NonNull String fileName, @NonNull byte[] bytes) throws IOException {

        if (!folder.exists()) {
            if(!folder.mkdirs()) {
                LogUtil.debug(mContext, "Каталог " + folder + " не создан");
            }
        }

        File file = new File(folder, fileName);

        FileOutputStream outputStream = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(outputStream);
        bos.write(bytes, 0, bytes.length);
        bos.flush();
        bos.close();
    }

    /**
     * Чтение информации о файле
     *
     * @param folder   папка
     * @param fileName имя файла
     * @return возвращается массив байтов
     * @throws IOException исключение
     */
    public byte[] readPath(@NonNull String folder, @NonNull String fileName) throws IOException {
        File dir = getRootCatalog(folder);
        File file = new File(dir, fileName);
        if (file.exists()) {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();

            byte[] data = new byte[BUFFER_SIZE];
            int count;
            while ((count = bis.read( data, 0, BUFFER_SIZE)) != -1) {
                buf.write(data, 0, count);
            }
            buf.flush();
            buf.close();
            return buf.toByteArray();
        } else {
            return null;
        }
    }

    /**
     * Копирование файлов
     * @param source источник
     * @param target назначение
     */
    public void copy(@NonNull File source, @NonNull File target) {
        try {
            try (InputStream in = new FileInputStream(source)) {
                try (OutputStream out = new FileOutputStream(target)) {
                    // Transfer bytes from in to out
                    byte[] buf = new byte[BUFFER_SIZE];
                    int len;
                    while ((len = in.read(buf)) > 0) {
                        out.write(buf, 0, len);
                    }
                }
            }
        } catch (IOException ignored) {

        }
    }

    /**
     * Доступен ли файл
     *
     * @param folder   папка
     * @param fileName имя файла
     * @return возвращается доступен ли файл
     */
    public boolean exists(@NonNull String folder, @NonNull String fileName) {
        File dir = getRootCatalog(folder);
        File file = new File(dir, fileName);
        return file.exists();
    }

    /**
     * удаление файла
     *
     * @param folder   имя папки
     * @param fileName имя файла
     */
    public void deleteFile(@NonNull String folder, @NonNull String fileName) {
        File dir = getRootCatalog(folder);
        if (!dir.exists()) {
            LogUtil.debug(mContext, "Корневая директория " + folder + " не найдена.");
        }
        File file = new File(dir, fileName);
        if (file.exists()) {
            deleteRecursive(mContext, file);
        } else {
            LogUtil.debug(mContext, "Файл " + fileName + " в директории " + folder + " не найден.");
        }
    }

    /**
     * очистка папки
     *
     * @param folder папка
     */
    public void deleteFolder(@NonNull String folder) {
        File dir = getRootCatalog(folder);
        if (dir.exists()) {
            deleteRecursive(mContext, dir);
        } else {
            LogUtil.debug(mContext, "Директория " + folder + " не найдена.");
        }
    }

    /**
     * удаление объекта File
     *
     * @param context контекст
     * @param fileOrDirectory файл или директория
     */
    public static boolean deleteRecursive(@NonNull Context context, @NonNull File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : Objects.requireNonNull(fileOrDirectory.listFiles())) {
                if (!deleteRecursive(context, child)) {
                    LogUtil.debug(context, "Директория " + child.getName() + " не удалена.");
                }
            }
        }
        return fileOrDirectory.delete();
    }
}
