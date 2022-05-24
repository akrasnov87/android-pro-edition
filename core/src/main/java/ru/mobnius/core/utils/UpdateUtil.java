package ru.mobnius.core.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.logger.Logger;
import ru.mobnius.core.ui.image.ImageViewActivity;

public class UpdateUtil {

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void getApk(final String url, final String apkName, final String packageName, final Context context, final Handler handler, final OnDownloadProgressListener listener) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                File folder = new File(Objects.requireNonNull(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).toString());

                File file = new File(folder.getAbsolutePath(), apkName);
                if (file.exists()) {
                    file.delete();
                }
                InputStream input = null;
                OutputStream output = null;
                HttpURLConnection connection = null;
                try {
                    URL sUrl = new URL(url);
                    connection = (HttpURLConnection) sUrl.openConnection();
                    connection.connect();
                    int fileLength = connection.getContentLength();
                    input = connection.getInputStream();
                    output = new FileOutputStream(file);
                    int progress = 0;
                    byte[] data = new byte[4096];
                    int count;
                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                        progress += count;
                        if (fileLength > 0) {
                            final int sendProgress = (progress * 100 / fileLength);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onProgress(sendProgress);
                                }
                            });
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onProgress(-1);
                            installApk(apkName, context, packageName);
                        }
                    });
                } catch (Exception e) {
                    listener.onProgress(-1);
                    e.printStackTrace();
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException ignored) {
                    }

                    if (connection != null)
                        connection.disconnect();
                }
            }
        });
        thread.start();
    }

    public static void installApk(String apkName, Context context, String packageName) {
        File folder = new File(Objects.requireNonNull(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)).toString());
        File file = new File(folder.getAbsolutePath(), apkName);
        final Uri uri = (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) ?
                FileProvider.getUriForFile(context, packageName + ".provider", file) : Uri.fromFile(file);
        Intent install = new Intent(Intent.ACTION_INSTALL_PACKAGE)
                .setData(uri)
                .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(install);
    }

    public static void getVideoFile(String fileName, final String Url, final Handler handler, final OnDownloadProgressListener listener) {
        final File fileFolder = FileManager.getInstance().getTempPictureFolder();
        if(!fileFolder.exists()) {
            fileFolder.mkdirs();
        }
        final File file = new File(fileFolder, fileName);
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection connection = null;
                InputStream input = null;
                OutputStream output = null;
                try {
                    URL sUrl = new URL(Url);
                    connection = (HttpURLConnection) sUrl.openConnection();
                    connection.connect();
                    if (connection.getResponseCode() != 200) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onErrorProgress("Не удалось установить соединение");
                            }
                        });
                    }
                    if (connection.getContentLength() <= 0) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onErrorProgress("Проблема с загрузкой файла");
                            }
                        });
                    }
                    input = connection.getInputStream();
                    output = new FileOutputStream(file);
                    int fileLength = connection.getContentLength();
                    int progress = 0;
                    byte[] data = new byte[4096];
                    int count;
                    while ((count = input.read(data)) != -1) {
                        output.write(data, 0, count);
                        progress += count;
                        if (fileLength > 0) {
                            final int sendProgress = (progress * 100 / fileLength);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    listener.onProgress(sendProgress);
                                }
                            });
                        }
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onFinishDownload(file);
                        }
                    });
                } catch (final Exception e) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            Logger.error(e);
                            Log.d("IMAG_ERR", e.toString());
                            listener.onErrorProgress("Ошибка с загрузкой файла");
                        }
                    });
                } finally {
                    try {
                        if (output != null)
                            output.close();
                        if (input != null)
                            input.close();
                    } catch (IOException exception) {
                        Logger.error(exception);
                    }
                    if (connection != null)
                        connection.disconnect();
                }
            }
        });
        thread.start();
    }

    public interface OnDownloadProgressListener {
        void onProgress(int progress);

        void onErrorProgress(String message);

        void onFinishDownload(File video);
    }
}
