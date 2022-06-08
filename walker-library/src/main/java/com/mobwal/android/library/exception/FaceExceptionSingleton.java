package com.mobwal.android.library.exception;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobwal.android.library.Constants;
import com.mobwal.android.library.util.LogUtilSingleton;
import com.mobwal.android.library.util.StringUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class FaceExceptionSingleton {
    private final String mExtension = ".exc";
    private final Context mContext;

    /**
     * Максимальное количество логов
     */
    public static int MAX_FILES = 3;

    private static FaceExceptionSingleton sFaceExceptionSingleton = null;

    public static FaceExceptionSingleton getInstance(@NonNull Context context) {
        if(sFaceExceptionSingleton == null) {
            return sFaceExceptionSingleton = new FaceExceptionSingleton(context);
        }else{
            return sFaceExceptionSingleton;
        }
    }

    /**
     *
     * @param context контекст
     */
    private FaceExceptionSingleton(@NonNull Context context) {
        mContext = context;
    }

    public void writeBytes(@NonNull String fileName, @NonNull byte[] bytes) {

        File[] files = mContext.getCacheDir().listFiles(pathname -> {
            if(pathname.isFile()) {
                String extension = StringUtil.getFileExtension(pathname.getName());
                return extension != null && extension.equals(".exc");
            } else {
                return false;
            }
        });

        if(files != null && files.length > MAX_FILES) {
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.compare(f2.lastModified(), f1.lastModified());
                }
            });

            for(int i = MAX_FILES; i < files.length; i++) {
                if(files[i].delete()) {
                    Log.d(Constants.TAG, "Файл " + files[i].getName() + " удалён");
                } else {
                    LogUtilSingleton.getInstance().writeText("Ошибка удаления файла лога: " + files[i].getName());
                }
            }
        }

        File file = new File(mContext.getCacheDir(), fileName);

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream (outputStream);
            bos.write(bytes, 0, bytes.length);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            LogUtilSingleton.getInstance().writeText( "Ошибка записи исключения в файл", e);
        }
    }

    public byte[] readPath(@NonNull String fileName) {
        File file = new File(mContext.getCacheDir(), fileName);
        if(file.exists()) {
            FileInputStream inputStream;
            try {
                inputStream = new FileInputStream(file);
                BufferedInputStream bis = new BufferedInputStream (inputStream);
                ByteArrayOutputStream buf = new ByteArrayOutputStream();
                int result = bis.read();
                while(result != -1) {
                    buf.write((byte) result);
                    result = bis.read();
                }
                return buf.toByteArray();
            } catch (IOException e) {
                LogUtilSingleton.getInstance().writeText("Ошибка чтения исключения из файла", e);
            }
        }

        return null;
    }

    public boolean exists(@NonNull String fileName) {
        File file = new File(mContext.getCacheDir(), fileName);
        return file.exists();
    }

    public void deleteFile(@NonNull String fileName) {
        File file = new File(mContext.getCacheDir(), fileName);
        if(!(file.exists() && file.delete())) {
            Log.d(Constants.TAG, "Ошибка удаления файла " + fileName);
        }
    }

    public void clearAll() {
        File[] files = mContext.getCacheDir().listFiles(pathname -> {
            if(pathname.isFile()) {
                String extension = StringUtil.getFileExtension(pathname.getName());
                return extension != null && extension.equals(mExtension);
            } else {
                return false;
            }
        });

        if(files != null) {
            for (File file: files) {
                if(!file.delete()) {
                    Log.d(Constants.TAG, "Ошибка удаления файла " + file.getName());
                }
            }
        }
    }

    /**
     * Получение количества файлов с ошибками
     * @return количество
     */
    public int getCount() {
        File[] files = mContext.getCacheDir().listFiles(pathname -> {
            if(pathname.isFile()) {
                String extension = StringUtil.getFileExtension(pathname.getName());
                return extension != null && extension.equals(mExtension);
            } else {
                return false;
            }
        });

        if(files != null) {
            return files.length;
        } else {
            return 0;
        }
    }

    @Nullable
    public List<FaceException> getExceptionList() {
        File[] files = mContext.getCacheDir().listFiles(pathname -> {
            if(pathname.isFile()) {
                String extension = StringUtil.getFileExtension(pathname.getName());
                return extension != null && extension.equals(mExtension);
            } else {
                return false;
            }
        });

        if(files != null) {
            List<FaceException> list = new ArrayList<>(files.length);
            for (File file : files) {
                byte[] bytes = readPath(file.getName());

                if (bytes != null) {
                    FaceException model = FaceException.toFace(new String(bytes));
                    if (model != null) {
                        list.add(model);
                    }
                }
            }

            if(list.size() > 0) {
                return list;
            } else {
                return null;
            }
        }
        return null;
    }

    @Nullable
    public FaceException getException(@NonNull String id) {
        List<FaceException> list = getExceptionList();
        if(list == null)
            return null;

        for(FaceException model : list){
            if(model.id.equals(id)){
                return model;
            }
        }
        return null;
    }

    @Nullable
    public FaceException getLastException() {
        List<FaceException> list = getExceptionList();
        if(list == null)
            return null;

        if(list.size() > 0) {
            if(list.size() == 1)
                return list.get(0);

            Collections.sort(list, new Comparator<FaceException>() {
                public int compare(FaceException o1, FaceException o2) {
                    return (int)o2.date.getTime() - (int)o1.date.getTime();
                }
            });
            return list.get(0);
        }

        return null;
    }
}
