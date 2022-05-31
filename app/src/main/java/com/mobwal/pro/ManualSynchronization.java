package com.mobwal.pro;

import android.annotation.SuppressLint;
import android.app.Activity;

import java.util.UUID;

import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.logger.Logger;

import com.mobwal.pro.data.Entity;
import com.mobwal.pro.data.EntityAttachment;
import com.mobwal.pro.data.FileTransferWebSocketSynchronization;
import com.mobwal.pro.data.IProgress;
import com.mobwal.pro.data.IProgressStep;
import com.mobwal.pro.data.utils.FullServerSidePackage;
import com.mobwal.pro.models.db.attachments;
import com.mobwal.pro.models.db.cd_results;
//import com.mobwal.pro.data.utils.FullServerSidePackage;
import ru.mobnius.core.utils.PackageReadUtils;

/**
 * Ручная синхронизация данных
 */
public class ManualSynchronization extends FileTransferWebSocketSynchronization {

    @SuppressLint("StaticFieldLeak")
    private static ManualSynchronization manualSynchronization;

    public static ManualSynchronization getInstance(WalkerSQLContext context, boolean zip) {
        if (manualSynchronization == null) {
            manualSynchronization = new ManualSynchronization(context, FileManager.getInstance(), zip);
        }
        return manualSynchronization;
    }

    public String totalTid;
    public String dictionaryTid;

    protected ManualSynchronization(WalkerSQLContext context, FileManager fileManager, boolean zip) {
        super(context, "MANUAL_SYNCHRONIZATION", fileManager, zip);
        oneOnlyMode = true;
        useAttachments = true;
        serverSidePackage = new FullServerSidePackage();
    }

    @Override
    public void initEntities() {
        totalTid = UUID.randomUUID().toString();
        dictionaryTid = UUID.randomUUID().toString();
        fileTid = UUID.randomUUID().toString();

        /*TableChangeUtil tableChangeUtil = new TableChangeUtil(isFullSync);

        addEntity(new EntityDictionary(RouteStatusesDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(RouteStatusesDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(AttachmentTypesDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(AttachmentTypesDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(PointTypesDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(PointTypesDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(ResultTypesDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(ResultTypesDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(SealPlacesDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(SealPlacesDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(SealTypesDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(SealTypesDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(RouteTypesDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(RouteTypesDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(ScalesDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(ScalesDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(ViolationsDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(ViolationsDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(FailureReasonDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(FailureReasonDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(RestrictionsDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(RestrictionsDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(DeviceLocationDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(DeviceLocationDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(PhaseDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(PhaseDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(TariffZoneDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(TariffZoneDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(WorkLinksDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(WorkLinksDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(DeviceTypesDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(DeviceTypesDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(VerificationReasonDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(VerificationReasonDao.TABLENAME)).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(UserInRoutesDao.TABLENAME, false, true).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(new EntityDictionary(BlankTypesDao.TABLENAME, false, true).setTid(dictionaryTid).setParam(getUserID(), getAppVersion()).setUseCFunction());

        addEntity(Entity.createInstance(TrackingDao.TABLENAME, true, false).setTid(totalTid).setClearable().setMany());
        addEntity(Entity.createInstance(AuditsDao.TABLENAME, true, false).setTid(totalTid).setClearable().setMany());
        addEntity(Entity.createInstance(MobileDevicesDao.TABLENAME, true, false).setTid(totalTid).setClearable());
        addEntity(Entity.createInstance(MobileIndicatorsDao.TABLENAME, true, false).setTid(totalTid).setClearable());
        addEntity(Entity.createInstance(ClientErrorsDao.TABLENAME, true, false).setTid(totalTid).setClearable());

        addEntity(Entity.createInstance(RouteHistoryDao.TABLENAME, false, true).setTid(totalTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(Entity.createInstance(EnergyTypesDao.TABLENAME, false, true).setChange(tableChangeUtil.getTableChange(EnergyTypesDao.TABLENAME)).setTid(dictionaryTid));
        addEntity(Entity.createInstance(RoutesDao.TABLENAME, false, true).setTid(totalTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(Entity.createInstance(UserPointsDao.TABLENAME, true, true).setTid(totalTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(Entity.createInstance(PointsDao.TABLENAME, true, true).setTid(totalTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(Entity.createInstance(ResultsDao.TABLENAME, true, true).setTid(totalTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(Entity.createInstance(UsersDao.TABLENAME, true, true).setTid(totalTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(Entity.createInstance(BlanksDao.TABLENAME, true, true).setTid(totalTid).setParam(getUserID(), getAppVersion()).setUseCFunction());
        addEntity(Entity.createInstance(SealsDao.TABLENAME, true, true).setTid(totalTid).setParam(getUserID(), getAppVersion()).setUseCFunction());

        addEntity(Entity.createInstance(InputMeterReadingsDao.TABLENAME, false, true).setTid(totalTid).setParam(getUserID(), getAppVersion(), "null").setUseCFunction());
        addEntity(Entity.createInstance(OutputMeterReadingsDao.TABLENAME, true, true).setTid(totalTid).setParam(getUserID(), getAppVersion(), "null").setUseCFunction());

        addEntity(Entity.createInstance(InputConnSealsDao.TABLENAME, false, true).setTid(totalTid).setParam(getUserID(), getAppVersion(), "null").setUseCFunction());
        addEntity(Entity.createInstance(OutputConnSealsDao.TABLENAME, true, true).setTid(totalTid).setParam(getUserID(), getAppVersion(), "null").setUseCFunction());

        addEntity(Entity.createInstance(InputTransformersDao.TABLENAME, false, true).setTid(totalTid).setParam(getUserID(), getAppVersion(), "null").setUseCFunction());
        addEntity(Entity.createInstance(OutputTransformersDao.TABLENAME, true, true).setTid(totalTid).setParam(getUserID(), getAppVersion(), "null").setUseCFunction());

        addEntity(new EntityAttachment(FilesDao.TABLENAME, true, true).setParam(getUserID(), getAppVersion()).setUseCFunction().setTid(fileTid));
        addEntity(new EntityAttachment(AttachmentsDao.TABLENAME, true, true).setParam(getUserID(), getAppVersion()).setUseCFunction().setTid(fileTid));

        addEntity(new EntityDictionary(TableChangeDao.TABLENAME, false, true).setParam(getUserID(), getAppVersion()).setUseCFunction().setTid(totalTid));*/

        addEntity(Entity.createInstance(cd_results.Meta.table, true, true).setTid(totalTid).setParam(getAppVersion()).setUseCFunction().setSchema("dbo"));
        addEntity(new EntityAttachment(attachments.Meta.table, true, true).setParam(getAppVersion()).setUseCFunction().setTid(fileTid).setSchema("dbo"));
    }

    @Override
    public void start(Activity activity, IProgress progress) {
        super.start(activity, progress);

        onProgress(IProgressStep.START, "пакет со справочниками " + dictionaryTid, null);
        onProgress(IProgressStep.START, "пакет общих данных " + totalTid, null);

        try {
            byte[] dictionaryBytes = generatePackage(dictionaryTid, (Object) null);
            sendBytes(dictionaryTid, dictionaryBytes);
            //AuditManager.getInstance().write(String.valueOf(dictionaryBytes.length), AuditListeners.TRAFFIC_OUTPUT, OnAuditListeners.Level.HIGH);
        } catch (Exception e) {
            onError(IProgressStep.START, "Ошибка обработки пакета для справочников. " + e.toString(), dictionaryTid);
        }
        try {
            byte[] totalBytes = generatePackage(totalTid, (Object) null);
            sendBytes(totalTid, totalBytes, () -> {
                try {
                    byte[] fileBytes = generatePackage(fileTid, (Object) null);
                    sendBytes(fileTid, fileBytes);
                    //AuditManager.getInstance().write(String.valueOf(fileBytes.length), AuditListeners.TRAFFIC_OUTPUT, OnAuditListeners.Level.HIGH);
                } catch (Exception e) {
                    onError(IProgressStep.START, "Ошибка обработки пакета с файлами. " + e.toString(), fileTid);
                }
            });
            //AuditManager.getInstance().write(String.valueOf(totalBytes.length), AuditListeners.TRAFFIC_OUTPUT, OnAuditListeners.Level.HIGH);
        } catch (Exception e) {
            onError(IProgressStep.START, "Ошибка обработки пакета для общих таблиц. " + e.toString(), totalTid);
        }

    }

    @Override
    public void onProcessingPackage(PackageReadUtils utils, String tid) {
        super.onProcessingPackage(utils, tid);

        try {
            //AuditManager.getInstance().write(String.valueOf(utils.getLength()), AuditListeners.TRAFFIC_INPUT, OnAuditListeners.Level.HIGH);
            //AuditManager.getInstance().write(String.valueOf(utils.getLength()), AuditListeners.SYNC_MNL_IN, OnAuditListeners.Level.LOW);
        } catch (Exception e) {
            Logger.error(e);
        }
    }

    @Override
    public void destroy() {
        super.destroy();

        manualSynchronization = null;
    }
}
