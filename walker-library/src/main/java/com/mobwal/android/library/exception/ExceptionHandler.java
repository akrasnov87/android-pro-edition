package com.mobwal.android.library.exception;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mobwal.android.library.Constants;
import com.mobwal.android.library.util.StringUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ExceptionHandler {
    private final String mExtension = ".exc";
    private final Context mContext;

    /**
     *
     * @param context контекст
     */
    public ExceptionHandler(@NonNull Context context) {
        mContext = context;
    }

    public void writeBytes(@NonNull String fileName, @NonNull byte[] bytes) {
        File file = new File(mContext.getCacheDir(), fileName);

        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream (outputStream);
            bos.write(bytes, 0, bytes.length);
            bos.flush();
            bos.close();
        } catch (IOException e) {
            e.printStackTrace();
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
                e.printStackTrace();
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
    public List<MaterialException> getExceptionList() {
        File[] files = mContext.getCacheDir().listFiles(pathname -> {
            if(pathname.isFile()) {
                String extension = StringUtil.getFileExtension(pathname.getName());
                return extension != null && extension.equals(mExtension);
            } else {
                return false;
            }
        });

        if(files != null) {
            List<MaterialException> list = new ArrayList<>(files.length);
            for (File file : files) {
                byte[] bytes = readPath(file.getName());

                if (bytes != null) {
                    MaterialException model = MaterialException.toFace(new String(bytes));
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
    public MaterialException getException(@NonNull String id) {
        List<MaterialException> list = getExceptionList();
        if(list == null)
            return null;

        for(MaterialException model : list){
            if(model.id.equals(id)){
                return model;
            }
        }
        return null;
    }

    @Nullable
    public MaterialException getLastException() {
        List<MaterialException> list = getExceptionList();
        if(list == null)
            return null;

        if(list.size() > 0) {
            if(list.size() == 1)
                return list.get(0);

            Collections.sort(list, new Comparator<MaterialException>() {
                public int compare(MaterialException o1, MaterialException o2) {
                    return (int)o2.date.getTime() - (int)o1.date.getTime();
                }
            });
            return list.get(0);
        }

        return null;
    }
}
