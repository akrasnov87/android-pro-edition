/*package ru.mobnius.cic.data.manager.synchronization;

import com.google.gson.JsonObject;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import ru.mobnius.core.data.DbOperationType;
import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.packager.BinaryBlock;
import ru.mobnius.core.data.packager.FileBinary;
import ru.mobnius.core.data.rpc.RPCRecords;
import ru.mobnius.core.data.rpc.RPCResult;
import ru.mobnius.core.data.rpc.RPCResultMeta;
import ru.mobnius.core.data.synchronization.utils.PackageResult;
import ru.mobnius.core.data.synchronization.utils.ServerSidePackage;
import ru.mobnius.core.utils.DateUtil;
import ru.mobnius.core.utils.LongUtil;
import ru.mobnius.cic.DbGenerate;
import ru.mobnius.cic.data.AuditListeners;
import ru.mobnius.cic.data.storage.models.Attachments;
import ru.mobnius.cic.data.storage.models.AttachmentsDao;
import ru.mobnius.cic.data.storage.models.Audits;
import ru.mobnius.cic.data.storage.models.AuditsDao;
import ru.mobnius.cic.data.storage.models.Tracking;
import ru.mobnius.cic.data.storage.models.TrackingDao;

public class ServerSidePackageUtilTest extends DbGenerate {

    @Before
    public void setUp() {
        getDaoSession().getAuditsDao().deleteAll();
        getDaoSession().getTrackingDao().deleteAll();
        getDaoSession().getAttachmentsDao().deleteAll();
    }

    @Test
    public void to() {
        String tid = UUID.randomUUID().toString();
        int blockTid = 1;
        String date = DateUtil.convertDateToString(new Date());

        Audits audit = new Audits();
        audit.fn_user = Long.parseLong(String.valueOf(GlobalSettings.DEFAULT_USER_ID));
        audit.c_type = AuditListeners.ON_AUTH;
        audit.d_date = date;
        audit.objectOperationType = DbOperationType.CREATED;
        audit.tid = tid;
        audit.blockTid = String.valueOf(blockTid);

        getDaoSession().getAuditsDao().insert(audit);
        audit = new Audits();
        audit.fn_user = Long.parseLong(String.valueOf(GlobalSettings.DEFAULT_USER_ID));
        audit.c_type = AuditListeners.ON_AUTH;
        audit.d_date = date;
        getDaoSession().getAuditsDao().insert(audit);

        RPCResult result = new RPCResult();
        RPCResultMeta resultMeta = new RPCResultMeta();
        resultMeta.success = true;
        result.meta = resultMeta;
        result.tid = blockTid;
        result.action = AuditsDao.TABLENAME;
        result.method = "Add";
        RPCRecords records = new RPCRecords();
        records.total = 1;
        JsonObject[] objects = new JsonObject[1];

        JsonObject object = new JsonObject();
        object.addProperty(AuditsDao.Properties.Fn_user.name, LongUtil.convertToLong(GlobalSettings.DEFAULT_USER_ID));
        object.addProperty(AuditsDao.Properties.D_date.name, DateUtil.convertDateToString(new Date()));
        object.addProperty(AuditsDao.Properties.C_type.name, AuditListeners.ON_AUTH);
        object.addProperty(AuditsDao.Properties.Tid.name, tid);
        object.addProperty(AuditsDao.Properties.ObjectOperationType.name, DbOperationType.CREATED);

        objects[0] = object;
        records.records = objects;
        result.result = records;

        MyServerSidePackageUtil sidePackage = new MyServerSidePackageUtil();
        PackageResult packageResult = sidePackage.to(getDaoSession(), result, tid);
        Assert.assertTrue(packageResult.success);

        List<Audits> audits = getDaoSession().getAuditsDao().queryBuilder().where(AuditsDao.Properties.IsSynchronization.eq(true)).list();
        Assert.assertEquals(audits.size(), 1);

        result.meta.success = false;
        result.meta.msg = "Test";
        packageResult = sidePackage.to(getDaoSession(), result, tid);
        Assert.assertFalse(packageResult.success);
    }

    @Test
    public void from() {
        String tid = UUID.randomUUID().toString();
        int blockTid = 0;
        String date = DateUtil.convertDateToString(new Date());

        Audits audit = new Audits();
        audit.fn_user = Long.parseLong(String.valueOf(GlobalSettings.DEFAULT_USER_ID));
        audit.c_type = AuditListeners.ON_AUTH;
        audit.d_date = date;
        audit.objectOperationType = DbOperationType.CREATED;
        audit.tid = tid;
        audit.blockTid = String.valueOf(blockTid);

        getDaoSession().getAuditsDao().insert(audit);
        audit = new Audits();
        audit.fn_user = Long.parseLong(String.valueOf(GlobalSettings.DEFAULT_USER_ID));
        audit.c_type = AuditListeners.ON_AUTH;
        audit.d_date = date;
        getDaoSession().getAuditsDao().insert(audit);

        RPCResult result = new RPCResult();
        RPCResultMeta resultMeta = new RPCResultMeta();
        resultMeta.success = true;
        result.meta = resultMeta;
        result.tid = blockTid;
        result.action = AuditsDao.TABLENAME;
        result.method = "Query";
        RPCRecords records = new RPCRecords();
        records.total = 1;
        JsonObject[] objects = new JsonObject[1];

        JsonObject object = new JsonObject();
        //object.put("id", audit.getId());
        object.addProperty(AuditsDao.Properties.Fn_user.name, LongUtil.convertToLong(GlobalSettings.DEFAULT_USER_ID));
        object.addProperty(AuditsDao.Properties.D_date.name, DateUtil.convertDateToString(new Date()));
        object.addProperty(AuditsDao.Properties.C_type.name, AuditListeners.ON_AUTH);
        object.addProperty(AuditsDao.Properties.Tid.name, tid);

        objects[0] = object;
        records.records = objects;
        result.result = records;

        MyServerSidePackageUtil sidePackage = new MyServerSidePackageUtil();
        sidePackage.setDeleteRecordBeforeAppend(true);
        PackageResult packageResult = sidePackage.from(getDaoSession(), result, tid, true);
        Assert.assertTrue(packageResult.success);
    }

    @Test
    public void fromDuplicate() {
        String tid = UUID.randomUUID().toString();
        int blockTid = 0;

        Tracking tracking2 = new Tracking();
        tracking2.d_date = DateUtil.convertDateToString(new Date());
        tracking2.n_longitude = 10;
        tracking2.n_latitude = 10;
        tracking2.c_network_status = "LTE";
        tracking2.fn_user = 1;
        tracking2.setObjectOperationType(DbOperationType.CREATED);
        tracking2.tid = tid;
        tracking2.blockTid = String.valueOf(blockTid);

        getDaoSession().getTrackingDao().insert(tracking2);
        Tracking tracking  = new Tracking();
        tracking.d_date = DateUtil.convertDateToString(new Date());
        tracking.n_longitude = 20;
        tracking.n_latitude = 20;
        tracking.c_network_status = "LTE";
        tracking.fn_user = 1;
        getDaoSession().getTrackingDao().insert(tracking);

        RPCResult result = new RPCResult();
        RPCResultMeta resultMeta = new RPCResultMeta();
        resultMeta.success = true;
        result.meta = resultMeta;
        result.tid = blockTid;
        result.action = TrackingDao.TABLENAME;
        result.method = "Query";
        RPCRecords records = new RPCRecords();
        records.total = 1;
        JsonObject[] objects = new JsonObject[1];

        JsonObject object = new JsonObject();
        object.addProperty(TrackingDao.Properties.Id.name, tracking2.getId());
        object.addProperty(TrackingDao.Properties.Fn_user.name, 2);
        object.addProperty(TrackingDao.Properties.D_date.name, DateUtil.convertDateToString(new Date()));
        object.addProperty(TrackingDao.Properties.N_longitude.name, 10);
        object.addProperty(TrackingDao.Properties.N_latitude.name, 10);

        objects[0] = object;
        records.records = objects;
        result.result = records;

        MyServerSidePackageUtil sidePackage = new MyServerSidePackageUtil();
        sidePackage.setDeleteRecordBeforeAppend(false);
        PackageResult packageResult = sidePackage.from(getDaoSession(), result, tid, true);
        Assert.assertTrue(packageResult.success);
        List<Tracking> trackings = getDaoSession().getTrackingDao().queryBuilder().list();
        Assert.assertEquals(trackings.size(), 2);
        trackings = getDaoSession().getTrackingDao().queryBuilder().where(TrackingDao.Properties.Id.eq(tracking2.getId())).list();
        Tracking t = trackings.toArray(new Tracking[0])[0];
        Assert.assertEquals(t.fn_user, tracking2.fn_user);
    }

    @Test
    public void attachmentsFromTest() throws IOException {
        String LINK = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();
        int blockTid = 0;

        BasicCredentials credentials = new BasicCredentials(GlobalSettings.DEFAULT_USER_NAME, GlobalSettings.DEFAULT_USER_PASSWORD);
        FileManager fileManager = FileManager.createInstance(credentials, getContext());
        try {
            fileManager.deleteFolder(FileManager.ATTACHMENTS);
        }catch (FileNotFoundException ignored){

        }

        fileManager.writeBytes(FileManager.ATTACHMENTS, "file1.tmp", "attachmentsToTest".getBytes());

        Attachments attachment = new Attachments();
        attachment.id = LINK;
        attachment.folder = FileManager.ATTACHMENTS;
        attachment.d_date = DateUtil.convertDateToString(new Date());
        attachment.c_name = "file1.tmp";
        attachment.objectOperationType = DbOperationType.CREATED;
        attachment.tid = tid;
        attachment.fn_type = 1;
        attachment.blockTid = String.valueOf(blockTid);

        getDaoSession().getAttachmentsDao().insert(attachment);

        RPCResult result = new RPCResult();
        RPCResultMeta resultMeta = new RPCResultMeta();
        resultMeta.success = true;
        result.meta = resultMeta;
        result.tid = blockTid;
        result.action = AttachmentsDao.TABLENAME;
        result.method = "Query";
        RPCRecords records = new RPCRecords();
        records.total = 1;
        JsonObject[] objects = new JsonObject[1];

        JsonObject object = new JsonObject();
        object.addProperty(AttachmentsDao.Properties.Id.name, LINK);
        object.addProperty(AttachmentsDao.Properties.Fn_type.name, 1);
        object.addProperty(AttachmentsDao.Properties.C_name.name, "file1.tmp");
        object.addProperty(AttachmentsDao.Properties.D_date.name, DateUtil.convertDateToString(new Date()));
        object.addProperty(AttachmentsDao.Properties.Folder.name, FileManager.ATTACHMENTS);

        objects[0] = object;
        records.records = objects;
        result.result = records;

        BinaryBlock binaryBlock = new BinaryBlock();
        binaryBlock.add("file1.tmp", LINK, "test".getBytes());
        FileBinary[] files = binaryBlock.getFiles();

        MyServerSidePackageUtil sidePackage = new MyServerSidePackageUtil();
        sidePackage.attachmentBy(fileManager);
        sidePackage.setFileBinary(files);
        PackageResult packageResult = sidePackage.from(getDaoSession(), result, tid, true, true);
        Assert.assertTrue(packageResult.message, packageResult.success);
        String txt = new String(fileManager.readPath(FileManager.ATTACHMENTS, "file1.tmp"));
        Assert.assertEquals(txt, "test");
    }

    static class MyServerSidePackageUtil extends ServerSidePackage {

    }
}*/