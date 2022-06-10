package com.mobwal.android.library;

import androidx.annotation.NonNull;

public interface LogListeners {
    void error(@NonNull String message);
    void error(@NonNull String message, @NonNull Exception e);
    void error(@NonNull Exception e);
    void debug(@NonNull String message);
    void info(@NonNull String message);
}
