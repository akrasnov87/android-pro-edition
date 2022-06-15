package com.mobwal.pro;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import java.util.UUID;

import com.mobwal.android.library.SimpleFileManager;
import com.mobwal.android.library.data.sync.Entity;
import com.mobwal.android.library.data.sync.EntityAttachment;
import com.mobwal.android.library.socket.SocketManager;
import com.mobwal.pro.models.db.Audit;
import com.mobwal.pro.models.db.MobileDevice;
import com.mobwal.pro.models.db.Setting;
import com.mobwal.android.library.data.sync.FileTransferWebSocketSynchronization;
import com.mobwal.android.library.data.sync.ProgressListeners;
import com.mobwal.android.library.data.sync.ProgressStep;
import com.mobwal.android.library.data.sync.util.FullServerSidePackage;
import com.mobwal.pro.models.db.Attachment;
import com.mobwal.pro.models.db.Point;
import com.mobwal.pro.models.db.Result;
import com.mobwal.pro.models.db.Route;
import com.mobwal.pro.models.db.Template;

/**
 * Ручная синхронизация данных
 */
public class ManualSynchronization extends FileTransferWebSocketSynchronization {

    @SuppressLint("StaticFieldLeak")
    private static ManualSynchronization manualSynchronization;

    public static ManualSynchronization getInstance(WalkerSQLContext context, SimpleFileManager simpleFileManager, boolean zip) {
        if (manualSynchronization == null) {
            manualSynchronization = new ManualSynchronization(context, simpleFileManager, zip);
        }
        return manualSynchronization;
    }

    public String totalTid;

    protected ManualSynchronization(WalkerSQLContext context, SimpleFileManager fileManager, boolean zip) {
        super(context, "MANUAL_SYNCHRONIZATION", fileManager, zip);
        oneOnlyMode = false;

        useAttachments = true;
        serverSidePackage = new FullServerSidePackage();
    }

    @Override
    public void initEntities() {
        totalTid = UUID.randomUUID().toString();
        fileTid = UUID.randomUUID().toString();

        addEntity(new Entity(Setting.class).setClearable().setParam(getAppVersion()).setTid(totalTid));
        addEntity(new Entity(Point.class).setClearable().setParam(getAppVersion()).setTid(totalTid));
        addEntity(new Entity(Route.class).setClearable().setParam(getAppVersion()).setTid(totalTid));
        addEntity(new Entity(Template.class).setClearable().setParam(getAppVersion()).setTid(totalTid));
        addEntity(new Entity(Result.class).setClearable().setParam(getAppVersion()).setTid(totalTid));

        addEntity(new Entity(Audit.class).setMany().setClearable().setParam(getAppVersion()).setTid(totalTid));
        addEntity(new Entity(MobileDevice.class).setMany().setClearable().setParam(getAppVersion()).setTid(totalTid));

        addEntity(new EntityAttachment(Attachment.class).setClearable().setParam(getAppVersion()).setTid(fileTid));
    }

    @Override
    public void start(@NonNull SocketManager socketManager, @NonNull ProgressListeners progress) {
        super.start(socketManager, progress);

        onProgress(ProgressStep.START, "пакет данных " + totalTid, null);
        onProgress(ProgressStep.START, "пакет байтов " + fileTid, null);

        try {
            byte[] dictionaryBytes = generatePackage(totalTid, (Object) null);
            sendBytes(totalTid, dictionaryBytes);
        } catch (Exception e) {
            onError(ProgressStep.START, "Ошибка обработки пакета данных. " + e, totalTid);
        }

        try {
            byte[] dictionaryBytes = generatePackage(fileTid, (Object) null);
            sendBytes(fileTid, dictionaryBytes);
        } catch (Exception e) {
            onError(ProgressStep.START, "Ошибка обработки пакета байтов. " + e, fileTid);
        }
    }

    @Override
    public void onError(int step, String message, String tid) {
        super.onError(step, message, tid);

        oneOnlyMode = true;
    }

    @Override
    public void destroy() {
        super.destroy();

        manualSynchronization = null;
    }
}
