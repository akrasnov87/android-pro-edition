package com.mobwal.pro;

import android.annotation.SuppressLint;
import android.app.Activity;

import com.mobwal.pro.data.Entity;
import com.mobwal.pro.data.IProgress;
import com.mobwal.pro.data.IProgressStep;
import com.mobwal.pro.data.WebSocketSynchronization;
import com.mobwal.pro.data.utils.PackageResult;
import com.mobwal.pro.data.utils.ToServerOnly;
import com.mobwal.pro.models.db.Result;
import com.mobwal.pro.utilits.ReflectionUtil;

import java.util.Date;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import ru.mobnius.core.data.rpc.RPCResult;
import ru.mobnius.core.utils.PackageCreateUtils;
import ru.mobnius.core.utils.PackageReadUtils;

/**
 * Механизм синхронизации служебных данных
 */
public class ServiceSynchronization extends WebSocketSynchronization {

    private long lastStartTime;

    @SuppressLint("StaticFieldLeak")
    private static ServiceSynchronization serviceSynchronization;

    public static ServiceSynchronization getInstance(WalkerSQLContext context, boolean zip) {
        if (serviceSynchronization != null) {
            return serviceSynchronization;
        } else {
            return serviceSynchronization = new ServiceSynchronization(context, zip);
        }
    }

    /**
     * конструктор
     *
     */
    protected ServiceSynchronization(WalkerSQLContext context, boolean zip) {
        super(context, "SERVICE_SYNCHRONIZATION", zip);
        lastStartTime = new Date().getTime();
        serverSidePackage = new ToServerOnly();
    }

    @Override
    public void start(Activity activity, IProgress progress) {
        super.start(activity, progress);
        long currentTime = new Date().getTime();
        if (currentTime < lastStartTime) {
            return;
        }
        if (isRunning()) {
            long diff = currentTime - lastStartTime;
            if (TimeUnit.HOURS.convert(diff, TimeUnit.MILLISECONDS) > 1) {
                stop();
                lastStartTime = currentTime;
            } else {
                return;
            }
        }
        // для каждой сущности генерируем свой пакет
        for (Entity entity : getEntities()) {
            byte[] bytes;
            try {
                bytes = generatePackage(entity.tid, entity.tableName);
                sendBytes(entity.tid, bytes);
                //AuditManager.getInstance().write(String.valueOf(bytes.length), AuditListeners.TRAFFIC_OUTPUT, OnAuditListeners.Level.HIGH);
            } catch (Exception e) {
                onError(IProgressStep.START, "Ошибка обработки пакета для таблицы " + entity.tableName + ". " + e.toString(), entity.tid);
            }
        }
    }

    @Override
    protected void onProcessingPackage(PackageReadUtils utils, String tid) {
        try {
            //AuditManager.getInstance().write(String.valueOf(utils.getLength()), AuditListeners.TRAFFIC_INPUT, OnAuditListeners.Level.HIGH);
            RPCResult[] rpcResults = utils.getToResult();
            for (RPCResult result : rpcResults) {
                PackageResult packageResult = serverSidePackage.to(getContext(), result, tid);
                if (!packageResult.success) {
                    oneOnlyMode = true;
                    onError(IProgressStep.RESTORE, packageResult.message, tid);
                } else {
                    oneOnlyMode = false;
                }
            }
        } catch (Exception e) {
            onError(IProgressStep.RESTORE, e, tid);
        }
    }

    @Override
    public byte[] generatePackage(String tid, Object... args) throws Exception {
        PackageCreateUtils utils = new PackageCreateUtils(isZip());
        String tableName = (String) args[0];
        if (tableName == null || tableName.isEmpty()) {
            throw new Exception("Имя таблицы в аргументах не передано.");
        }
        processingPackageTo(utils, tableName, tid);
        return utils.generatePackage(tid);
    }

    @Override
    protected void initEntities() {
        addEntity(new Entity(ReflectionUtil.getTableName(Result.class)).setTid(UUID.randomUUID().toString()).setSchema("dbo"));
        //addEntity(new Entity(AuditsDao.TABLENAME).setTid(UUID.randomUUID().toString()));
        //addEntity(new Entity(MobileDevicesDao.TABLENAME).setTid(UUID.randomUUID().toString()));
        //addEntity(new Entity(MobileIndicatorsDao.TABLENAME).setTid(UUID.randomUUID().toString()));
        //addEntity(new Entity(ClientErrorsDao.TABLENAME).setTid(UUID.randomUUID().toString()));
    }
}
