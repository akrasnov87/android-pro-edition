package com.mobwal.pro.utilits;

import android.content.Context;

import com.mobwal.pro.WalkerApplication;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

public class LogUtil {
    public static int FILE_SIZE = 15; // 512Кб
    public static int MAX_FILES = 3;

    public static void writeText(Context context, String message) {
        File[] files = context.getCacheDir().listFiles(pathname -> {
            if(pathname.isFile()) {
                String extension = StringUtil.getFileExtension(pathname.getName());
                return extension != null && extension.equals(".log");
            } else {
                return false;
            }
        });

        if (files == null || files.length == 0) {
            emptyFile(context, message);
        } else {
            Arrays.sort(files, new Comparator<File>() {
                public int compare(File f1, File f2) {
                    return Long.compare(f2.lastModified(), f1.lastModified());
                }
            });
            File file = files[0];

            if(file.length() > FILE_SIZE) {
                emptyFile(context, message);
                return;
            }

            message = "\n" + message;

            if(files.length > MAX_FILES) {
                for(int i = MAX_FILES; i < files.length; i++) {
                    if(!files[i].delete()) {
                        write(file, ("Ошибка удаления файла лога: " + files[i].getName()).getBytes(StandardCharsets.UTF_8));
                    }
                }
            }

            write(file, message.getBytes(StandardCharsets.UTF_8));
        }
    }

    private static void emptyFile(Context context, String message) {
        write(new File(context.getCacheDir(), DateUtil.convertDateToSystemString(new Date()) + ".log"), message.getBytes(StandardCharsets.UTF_8));
    }

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
