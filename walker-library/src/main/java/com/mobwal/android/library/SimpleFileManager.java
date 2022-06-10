package com.mobwal.android.library;

import androidx.annotation.NonNull;

import com.mobwal.android.library.authorization.credential.BasicCredential;

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

    public File getEnvironment() {
        return mEnvironment;
    }

    /**
     * Хранение данных
     *
     * @param environment директория из context.getFileDir() | context.getCacheDir()
     */
    public SimpleFileManager(@NonNull File environment) {
        mEnvironment = environment;
    }

    /**
     * Хранение данных
     *
     * @param environment директория из context.getFileDir() | context.getCacheDir()
     * @param credential информации об авторизованном пользователе
     */
    public SimpleFileManager(@NonNull File environment, @NonNull BasicCredential credential) {
        this(new File(environment, credential.login));
    }

    /**
     * Запись байтов в файловую систему
     *
     * @param fileName имя файла
     * @param bytes    массив байтов
     * @throws IOException исключение
     */
    public void writeBytes(@NonNull String fileName, @NonNull byte[] bytes) throws IOException {
        if (!mEnvironment.exists()) {
            if(!mEnvironment.mkdirs()) {
                LogManager.getInstance().debug( "Каталог " + mEnvironment.getName() + " не создан");
            }
        }

        File file = new File(mEnvironment, fileName);

        FileOutputStream outputStream = new FileOutputStream(file);
        BufferedOutputStream bos = new BufferedOutputStream(outputStream);
        bos.write(bytes, 0, bytes.length);
        bos.flush();
        bos.close();
    }

    /**
     * Чтение информации о файле
     *
     * @param fileName имя файла
     * @return возвращается массив байтов
     * @throws IOException исключение
     */
    public byte[] readPath(@NonNull String fileName) throws IOException {
        File file = new File(mEnvironment, fileName);
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
     * @param fileName имя файла
     * @return возвращается доступен ли файл
     */
    public boolean exists(@NonNull String fileName) {
        File file = new File(mEnvironment, fileName);
        return file.exists();
    }

    /**
     * удаление файла
     *
     * @param fileName имя файла
     */
    public void deleteFile(@NonNull String fileName) {
        if (!mEnvironment.exists()) {
            LogManager.getInstance().debug("Корневая директория " + mEnvironment.getName() + " не найдена.");
        }
        File file = new File(mEnvironment, fileName);
        if (file.exists()) {
            deleteRecursive(file);
        } else {
            LogManager.getInstance().debug("Файл " + fileName + " в директории " + mEnvironment.getName() + " не найден.");
        }
    }

    /**
     * очистка папки
     */
    public void deleteFolder() {
        if (mEnvironment.exists()) {
            deleteRecursive(mEnvironment);
        } else {
            LogManager.getInstance().debug("Директория " + mEnvironment.getName() + " не найдена.");
        }
    }

    /**
     * удаление объекта File
     *
     * @param fileOrDirectory файл или директория
     */
    public static boolean deleteRecursive(@NonNull File fileOrDirectory) {
        if (fileOrDirectory.isDirectory()) {
            for (File child : Objects.requireNonNull(fileOrDirectory.listFiles())) {
                if (!deleteRecursive(child)) {
                    LogManager.getInstance().debug("Директория " + child.getName() + " не удалена.");
                }
            }
        }
        return fileOrDirectory.delete();
    }
}
