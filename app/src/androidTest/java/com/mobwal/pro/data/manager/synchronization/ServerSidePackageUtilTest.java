package com.mobwal.pro.data.manager.synchronization;

import com.google.gson.JsonObject;
import com.mobwal.pro.data.DbGenerate;
import com.mobwal.pro.data.utils.PackageResult;
import com.mobwal.pro.data.utils.ServerSidePackage;
import com.mobwal.pro.models.db.attachments;
import com.mobwal.pro.models.db.cd_results;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
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
import ru.mobnius.core.data.storage.FieldNames;

public class ServerSidePackageUtilTest extends DbGenerate {

    @Before
    public void setUp() {
        getSQLContext().exec("DELETE FROM " + cd_results.Meta.table, new Object[0]);
        getSQLContext().exec("DELETE FROM " + attachments.Meta.table, new Object[0]);
    }

    @After
    public void tearDown() {
        getSQLContext().trash();
    }

    @Test
    public void to() {
        String tid = UUID.randomUUID().toString();
        int blockTid = 1;

        cd_results item = new cd_results(UUID.randomUUID().toString());
        item.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;
        item.__TID = tid;
        item.__BLOCK_TID = String.valueOf(blockTid);

        getSQLContext().insert(item);

        item = new cd_results(UUID.randomUUID().toString());

        getSQLContext().insert(item);

        RPCResult result = new RPCResult();
        RPCResultMeta resultMeta = new RPCResultMeta();
        resultMeta.success = true;
        result.meta = resultMeta;
        result.tid = blockTid;
        result.action = cd_results.Meta.table;
        result.method = "Add";
        RPCRecords records = new RPCRecords();
        records.total = 1;
        JsonObject[] objects = new JsonObject[1];

        JsonObject object = new JsonObject();
        object.addProperty(FieldNames.TID, tid);
        object.addProperty(FieldNames.OBJECT_OPERATION_TYPE, DbOperationType.CREATED);

        objects[0] = object;
        records.records = objects;
        result.result = records;

        MyServerSidePackageUtil sidePackage = new MyServerSidePackageUtil();
        PackageResult packageResult = sidePackage.to(getSQLContext(), result, tid);
        Assert.assertTrue(packageResult.success);

        Collection<cd_results> results = getSQLContext().select("select * from " + cd_results.Meta.table + " where " + FieldNames.IS_SYNCHRONIZATION + " = 1", new String[0], cd_results.class);
        Assert.assertEquals(results.size(), 1);

        result.meta.success = false;
        result.meta.msg = "Test";
        packageResult = sidePackage.to(getSQLContext(), result, tid);
        Assert.assertFalse(packageResult.success);
    }

    @Test
    public void from() {
        String tid = UUID.randomUUID().toString();
        int blockTid = 0;

        cd_results item = new cd_results(UUID.randomUUID().toString());
        item.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;
        item.__TID = tid;
        item.__BLOCK_TID = String.valueOf(blockTid);
        getSQLContext().insert(item);

        item = new cd_results(UUID.randomUUID().toString());
        getSQLContext().insert(item);

        RPCResult result = new RPCResult();
        RPCResultMeta resultMeta = new RPCResultMeta();
        resultMeta.success = true;
        result.meta = resultMeta;
        result.tid = blockTid;
        result.action = cd_results.Meta.table;
        result.method = "Query";
        RPCRecords records = new RPCRecords();
        records.total = 1;
        JsonObject[] objects = new JsonObject[1];

        JsonObject object = new JsonObject();
        object.addProperty(FieldNames.TID, tid);
        object.addProperty(FieldNames.OBJECT_OPERATION_TYPE, DbOperationType.CREATED);

        objects[0] = object;
        records.records = objects;
        result.result = records;

        MyServerSidePackageUtil sidePackage = new MyServerSidePackageUtil();
        sidePackage.setDeleteRecordBeforeAppend(true);
        PackageResult packageResult = sidePackage.from(getSQLContext(), result, tid, true);
        Assert.assertTrue(packageResult.success);
    }

    @Test
    public void fromDuplicate() {
        String tid = UUID.randomUUID().toString();
        int blockTid = 0;

        cd_results item = new cd_results(UUID.randomUUID().toString());
        item.jb_data = item.id;
        item.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;
        item.__TID = tid;
        item.__BLOCK_TID = String.valueOf(blockTid);
        getSQLContext().insert(item);

        item = new cd_results(UUID.randomUUID().toString());
        item.jb_data = item.id;
        getSQLContext().insert(item);

        RPCResult result = new RPCResult();
        RPCResultMeta resultMeta = new RPCResultMeta();
        resultMeta.success = true;
        result.meta = resultMeta;
        result.tid = blockTid;
        result.action = cd_results.Meta.table;
        result.method = "Query";
        RPCRecords records = new RPCRecords();
        records.total = 1;
        JsonObject[] objects = new JsonObject[1];

        JsonObject object = new JsonObject();
        object.addProperty("id", item.id);
        object.addProperty("jb_data", "new");
        object.addProperty(FieldNames.TID, tid);
        object.addProperty(FieldNames.BLOCK_TID, String.valueOf(blockTid));

        objects[0] = object;
        records.records = objects;
        result.result = records;

        MyServerSidePackageUtil sidePackage = new MyServerSidePackageUtil();
        sidePackage.setDeleteRecordBeforeAppend(false);
        PackageResult packageResult = sidePackage.from(getSQLContext(), result, tid, true);
        Assert.assertTrue(packageResult.success);
        Collection<cd_results> results = getSQLContext().select("select * from " + cd_results.Meta.table, new String[0], cd_results.class); // getDaoSession().getTrackingDao().queryBuilder().list();
        Assert.assertEquals(results.size(), 2);
        results = getSQLContext().select("select * from " + cd_results.Meta.table + " where id = ?", new String[] { item.id }, cd_results.class); // getDaoSession().getTrackingDao().queryBuilder().where(TrackingDao.Properties.Id.eq(tracking2.getId())).list();
        cd_results t = results.toArray(new cd_results[0])[0];
        Assert.assertEquals(t.id, item.id);
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

        attachments attachment = new attachments();
        attachment.id = LINK;
        attachment.c_path = "file1.tmp";
        attachment.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;
        attachment.__TID = tid;
        attachment.__BLOCK_TID = String.valueOf(blockTid);

        getSQLContext().insert(attachment);

        RPCResult result = new RPCResult();
        RPCResultMeta resultMeta = new RPCResultMeta();
        resultMeta.success = true;
        result.meta = resultMeta;
        result.tid = blockTid;
        result.action = attachments.Meta.table;
        result.method = "Query";
        RPCRecords records = new RPCRecords();
        records.total = 1;
        JsonObject[] objects = new JsonObject[1];

        JsonObject object = new JsonObject();
        object.addProperty("id", LINK);
        object.addProperty(FieldNames.C_NAME, "file1.tmp");

        objects[0] = object;
        records.records = objects;
        result.result = records;

        BinaryBlock binaryBlock = new BinaryBlock();
        binaryBlock.add("file1.tmp", LINK, "test".getBytes());
        FileBinary[] files = binaryBlock.getFiles();

        MyServerSidePackageUtil sidePackage = new MyServerSidePackageUtil();
        sidePackage.attachmentBy(fileManager);
        sidePackage.setFileBinary(files);
        PackageResult packageResult = sidePackage.from(getSQLContext(), result, tid, true, true);
        Assert.assertTrue(packageResult.message, packageResult.success);
        String txt = new String(fileManager.readPath(FileManager.ATTACHMENTS, "file1.tmp"));
        Assert.assertEquals(txt, "attachmentsToTest");
    }

    static class MyServerSidePackageUtil extends ServerSidePackage {

    }
}