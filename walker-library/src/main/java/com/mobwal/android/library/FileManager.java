package com.mobwal.android.library;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.mobwal.android.library.authorization.credential.BasicCredential;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * Работа с файловой системой
 */
@Deprecated
public class FileManager {
    public static int BUFFER_SIZE = 2048;

    public static String APP_NAME = "Mobnius";
    /**
     * папка для хранение вложений
     */
    public static final String ATTACHMENTS = "attachments";

    /**
     * папка для хранения необработанных временных фотографий
     */
    public static final String TEMPORARY = "temporary_pictures";

    /**
     * папка для хранение файлов
     */
    public static final String FILES = "files";

    /**
     * папка для временных изображений
     */
    public static final String PHOTOS = "temp";

    private final BasicCredential credentials;
    @SuppressLint("StaticFieldLeak")
    private static FileManager fileManager;
    private Context context;

    /**
     * Конструктор
     *
     * @param context     контекст
     * @param credentials информация о пользователе
     */
    public FileManager(BasicCredential credentials, Context context) {
        this.credentials = credentials;
        this.context = context;
    }

    /**
     * Создание экземпляра файлового менеджера
     *
     * @param credentials авторизация
     * @param context     контекст
     * @return Объект для работы с файловой системой
     */
    public static FileManager createInstance(BasicCredential credentials, Context context) {
        return fileManager = new FileManager(credentials, context);
    }

    /**
     * получение текущего экземпляра
     *
     * @return Объект для работы с файловой системой
     */
    public static FileManager getInstance() {
        return fileManager;
    }

    private File getRootCatalog(String folder) {
        return new File(context.getFilesDir(), APP_NAME + "/" + credentials.login + "/" + folder);
    }

    /**
     * Каталог с необработанными фото
     *
     * @return возвращается путь к папке
     */
    public File getTemporaryFolder() {
        return getRootCatalog(TEMPORARY);
    }

    /**
     * Каталог с вложениями полученными в результате синхронизации
     *
     * @return возвращается путь к папке
     */
    public File getAttachmentsFolder() {
        return getRootCatalog(ATTACHMENTS);
    }

    /**
     * каталог для хранения временных файлов изображений
     *
     * @return возвращается путь к папке
     */
    public File getTempPictureFolder() {
        return getRootCatalog(PHOTOS);
    }


    /**
     * Запись байтов в файловую систему
     *
     * @param folder   папка
     * @param fileName имя файла
     * @param bytes    массив байтов
     * @throws IOException исключение
     */
    public void writeBytes(String folder, String fileName, byte[] bytes) throws IOException {

        File dir = getRootCatalog(folder);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                Log.d(Constants.TAG, "Каталог " + folder + " не создан");
            }
        }

        File file = new File(dir, fileName);

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
    public byte[] readPath(String folder, String fileName) throws IOException {
        File dir = getRootCatalog(folder);
        File file = new File(dir, fileName);
        if (file.exists()) {
            FileInputStream inputStream = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            ByteArrayOutputStream buf = new ByteArrayOutputStream();

            byte[] data = new byte[BUFFER_SIZE];
            int count;
            while ((count = bis.read(data, 0, BUFFER_SIZE)) != -1) {
                buf.write(data, 0, count);
            }
            buf.flush();
            buf.close();
            /*int result = bis.read();
            while (result != -1) {
                buf.write((byte) result);
                result = bis.read();
            }*/
            bis.close();
            return buf.toByteArray();
        } else {
            return null;
        }
    }

    /**
     * Доступен ли файл
     *
     * @param folder   папка
     * @param fileName имя файла
     * @return возвращается доступен ли файл
     */
    public boolean exists(String folder, String fileName) {
        File dir = getRootCatalog(folder);
        File file = new File(dir, fileName);
        return file.exists();
    }

    /**
     * удаление файла
     *
     * @param folder   имя папки
     * @param fileName имя файла
     * @throws FileNotFoundException исключение при отсуствие директории или файла
     */
    public void deleteFile(String folder, String fileName) throws FileNotFoundException {
        File dir = getRootCatalog(folder);
        if (!dir.exists()) {
            throw new FileNotFoundException("Корневая директория " + folder + " не найдена.");
        }
        File file = new File(dir, fileName);
        if (file.exists()) {
            deleteRecursive(file);
        } else {
            throw new FileNotFoundException("Файл " + fileName + " в директории " + folder + " не найден.");
        }
    }

    /**
     * очистка папки
     *
     * @param folder папка
     */
    public void deleteFolder(String folder) throws FileNotFoundException {
        File dir = getRootCatalog(folder);
        if (dir.exists()) {
            deleteRecursive(dir);
        } else {
            throw new FileNotFoundException("Директория " + folder + " не найдена.");
        }
    }

    /**
     * удаление объекта File
     *
     * @param fileOrDirectory файл или директория
     */
    private boolean deleteRecursive(File fileOrDirectory) {
        if (fileOrDirectory.isDirectory())
            for (File child : Objects.requireNonNull(fileOrDirectory.listFiles()))
                deleteRecursive(child);

        return fileOrDirectory.delete();
    }

    /**
     * удаление пользовательской папки
     */
    public void clearUserFolder() {
        File dir = new File(Environment.getExternalStorageDirectory(), credentials.login);
        deleteRecursive(dir);
    }

    public void destroy() {
        fileManager = null;
    }
}
