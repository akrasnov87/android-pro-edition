package com.mobwal.android.library;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Логирование информации в файловой системе
 */
public class LogManager {
    private static LogListeners sLogManager;

    public static void createInstance(@NonNull LogListeners listeners) {
        sLogManager = listeners;
    }

    public static LogListeners getInstance() {
        return sLogManager;
    }
}
