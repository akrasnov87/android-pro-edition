package com.mobwal.pro;

import androidx.annotation.NonNull;

import com.mobwal.android.library.LogListeners;
import com.mobwal.android.library.data.DbOperationType;
import com.mobwal.android.library.util.StringUtil;
import com.mobwal.pro.models.db.Audit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LogInMemory implements LogListeners {
    private final List<Audit> mAudits;
    private final String mSessionId;

    public List<Audit> getAudits() {
        return mAudits;
    }

    public LogInMemory(String sessionId) {
        mAudits = new ArrayList<>();
        mSessionId = sessionId;
    }

    @Override
    public void error(@NonNull String message) {
        writeInDb("ERROR", message);
    }

    @Override
    public void error(@NonNull String message, @NonNull Exception e) {
        writeInDb("ERROR", message + "\n" + StringUtil.exceptionToString(e));
    }

    @Override
    public void error(@NonNull Exception e) {
        writeInDb("ERROR", StringUtil.exceptionToString(e));
    }

    @Override
    public void debug(@NonNull String message) {
        writeInDb("DEBUG", message);
    }

    @Override
    public void info(@NonNull String message) {
        writeInDb("INFO", message);
    }

    private void writeInDb(String type, String data) {
        Audit audit = new Audit();
        audit.__IS_SYNCHRONIZATION = false;
        audit.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;
        audit.c_session_id = mSessionId;
        audit.d_date = new Date();
        audit.c_type = type;
        audit.c_data = data;

        mAudits.add(audit);
    }
}
