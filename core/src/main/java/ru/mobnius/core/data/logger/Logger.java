package ru.mobnius.core.data.logger;

import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

import ru.mobnius.core.data.audit.AuditManager;
import ru.mobnius.core.data.audit.OnAuditListeners;

/**
 * Запись и хранение логов
 */
public class Logger {
    /**
     * тег для поиска логов
     */
    private final static String TAG = "MyLogs";

    /**
     * Запись ошибки в лог
     *
     * @param e ошибка
     */
    public static void error(Exception e) {
        if (e.toString().contains("JsonSyntaxException") || e.toString().contains("FileNotFoundException")) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            String info = sw.toString();
            String c_type = e.toString().contains("JsonSyntaxException") ? "JSON_SYNTAX" : "FILE_NOT_FOUND";
            AuditManager.getInstance().write(info, c_type, OnAuditListeners.Level.HIGH);
        } else {
            AuditManager.getInstance().write(e.toString(), "ERROR", OnAuditListeners.Level.HIGH);
            e.printStackTrace();
        }
    }


    /**
     * Запись ошибки с описанием
     *
     * @param description описание ошибки
     * @param e           ошибка
     */
    public static void error(String description, Exception e) {
        AuditManager.getInstance().write(e.toString(), "ERROR", OnAuditListeners.Level.HIGH);
        e.printStackTrace();
    }

    /**
     * Запись ошибки с описанием
     *
     * @param type    тип для отслеживания в БД
     * @param message сообщение
     */
    public static void statement(String type, String message) {
        AuditManager.getInstance().write(message, type, OnAuditListeners.Level.HIGH);

    }

    /**
     * Информация для отладки
     *
     * @param msg текст
     */
    public static void debug(String msg) {
        Log.d(TAG, msg);
    }
}