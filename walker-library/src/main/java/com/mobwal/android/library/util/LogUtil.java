package com.mobwal.android.library.util;

import android.content.Context;
import android.util.Log;

import androidx.annotation.Nullable;

import com.mobwal.android.library.ZipManager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

/**
 * Логирование информации в файловой системе
 */
public class LogUtil {
    /**
     * Максимальный размер лога в килобайтах
     */
    public static int FILE_SIZE = 512 * 1024; // 512Кб

    /**
     * Максимальное количество логов
     */
    public static int MAX_FILES = 3;

    static String TAG = "LOG_UTIL";

    @Nullable
    public static File getArchiveLog(Context context) {
        File[] files = context.getCacheDir().listFiles(pathname -> {
            if(pathname.isFile()) {
                String extension = StringUtil.getFileExtension(pathname.getName());
                return extension != null && extension.equals(".log");
            } else {
                return false;
            }
        });
        if (files != null) {
            File file = new File(context.getCacheDir(), "journal.zip");
            ZipManager.zipFiles(context, files, file.getAbsolutePath(), null);
            return file;
        }
        return null;
    }

    /**
     * Запись текста в файл лога
     * @param context контекст
     * @param message текст сообщения
     */
    public static void writeText(Context context, String message) {
        File[] files = context.getCacheDir().listFiles(pathname -> {
            if(pathname.isFile()) {
                String extension = StringUtil.getFileExtension(pathname.getName());
                return extension != null && extension.equals(".log");
            } else {
                return false;
            }
        });

        File[] smalls = context.getCacheDir().listFiles(pathname -> {
            if(pathname.isFile()) {
                String extension = StringUtil.getFileExtension(pathname.getName());
                return extension != null && pathname.length() < FILE_SIZE && extension.equals(".log");
            } else {
                return false;
            }
        });

        if (smalls == null || smalls.length == 0) {
            createEmptyFile(context, message);
        } else {
            Arrays.sort(smalls, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.compare(f2.lastModified(), f1.lastModified());
                }
            });

            File file = smalls[0];
            message = "\n" + message;
            write(file, message.getBytes(StandardCharsets.UTF_8));

            if(files != null && files.length > MAX_FILES) {
                Arrays.sort(files, new Comparator<File>() {
                    public int compare(File f1, File f2) {
                        return Long.compare(f2.lastModified(), f1.lastModified());
                    }
                });

                for(int i = MAX_FILES; i < files.length; i++) {
                    if(files[i].delete()) {
                        Log.d(TAG, "Файл " + files[i].getName() + " удалён");
                    } else {
                        write(file, ("Ошибка удаления файла лога: " + files[i].getName()).getBytes(StandardCharsets.UTF_8));
                    }
                }
            }
        }
    }

    /**
     * Данные для вывода в режиме отладки
     * @param context контекст
     * @param message текст сообщения
     */
    public static void debug(Context context, String message) {
        writeText(context, message);
    }

    /**
     * Запись текста в файл лога
     * @param context контекст
     * @param message текст сообщения
     * @param e исключение
     */
    public static void writeText(Context context, String message, Exception e) {
        writeText(context, message + " " + e.toString());
    }

    /**
     * Создание пустой строки
     * @param context контекст
     * @param message текст сообщения
     */
    private static void createEmptyFile(Context context, String message) {
        String fileName = new Date().getTime() + ".log";
        File file = new File(context.getCacheDir(), fileName);
        write(file, message.getBytes(StandardCharsets.UTF_8));
        Log.d(TAG, "Создан пустой файл " + fileName);
    }

    /**
     * Запись данные в файл
     * @param file файл
     * @param bytes массив байтов
     */
    private static void write(File file, byte[] bytes) {
        try {
            FileOutputStream outputStream = new FileOutputStream(file, true);
            BufferedOutputStream bos = new BufferedOutputStream(outputStream);
            bos.write(bytes, 0, bytes.length);
            bos.flush();
            bos.close();
        } catch (Exception ignore) {

        }
    }
}
