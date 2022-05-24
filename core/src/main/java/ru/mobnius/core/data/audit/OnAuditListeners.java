package ru.mobnius.core.data.audit;

public interface OnAuditListeners {
    /**
     * Уровни важности
     */
    enum Level {
        LOW,
        HIGH
    }

    void onAuditWrite(String message, String type, Level level);
}
