package ru.mobnius.core.data.audit;

public class AuditManager {
    private static AuditManager sAuditManager;

    public static void createInstance(OnAuditListeners listeners) {
        sAuditManager = new AuditManager(listeners);
    }

    public static AuditManager getInstance() {
        if(sAuditManager == null) {
            return sAuditManager = new AuditManager(new OnAuditListeners() {
                @Override
                public void onAuditWrite(String message, String type, Level level) {

                }
            });
        }
        return sAuditManager;
    }
    private OnAuditListeners mListeners;

    private AuditManager(OnAuditListeners listeners) {
        mListeners = listeners;
    }

    /**
     * запись информации в базу данных
     * @param message сообщение
     * @param type тип сообщения
     * @param level уровень сообщения
     */
    public void write(String message, String type, OnAuditListeners.Level level) {
        mListeners.onAuditWrite(message, type, level);
    }
}
