package com.mobwal.pro;

import com.google.gson.JsonObject;
import com.mobwal.android.library.FieldNames;
import com.mobwal.android.library.SimpleFileManager;
import com.mobwal.android.library.data.DbOperationType;
import com.mobwal.android.library.util.ReflectionUtil;
import com.mobwal.android.library.data.sync.util.PackageResult;
import com.mobwal.android.library.data.sync.util.ServerSidePackage;
import com.mobwal.pro.models.db.Attachment;
import com.mobwal.pro.models.db.Result;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import com.mobwal.android.library.authorization.credential.BasicCredential;
import com.mobwal.android.library.data.packager.BinaryBlock;
import com.mobwal.android.library.data.packager.FileBinary;
import com.mobwal.android.library.data.rpc.RPCRecords;
import com.mobwal.android.library.data.rpc.RPCResult;
import com.mobwal.android.library.data.rpc.RPCResultMeta;

public class ServerSidePackageUtilTest extends DbGenerate {

    @Before
    public void setUp() {
        getSQLContext().exec("DELETE FROM " + ReflectionUtil.getTableName(Result.class), new Object[0]);
        getSQLContext().exec("DELETE FROM " + ReflectionUtil.getTableName(Attachment.class), new Object[0]);
    }

    @After
    public void tearDown() {
        destroy();
    }

    @Test
    public void to() {
        String tid = UUID.randomUUID().toString();
        int blockTid = 1;

        Result item = new Result(UUID.randomUUID().toString());
        item.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;
        item.__TID = tid;
        item.__BLOCK_TID = String.valueOf(blockTid);

        getSQLContext().insert(item);

        item = new Result(UUID.randomUUID().toString());

        getSQLContext().insert(item);

        RPCResult result = new RPCResult();
        RPCResultMeta resultMeta = new RPCResultMeta();
        resultMeta.success = true;
        result.meta = resultMeta;
        result.tid = blockTid;
        result.action = ReflectionUtil.getTableName(Result.class);
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

        Collection<Result> results = getSQLContext().select("select * from " + ReflectionUtil.getTableName(Result.class) + " where " + FieldNames.IS_SYNCHRONIZATION + " = 1", new String[0], Result.class);
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

        Result item = new Result(UUID.randomUUID().toString());
        item.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;
        item.__TID = tid;
        item.__BLOCK_TID = String.valueOf(blockTid);
        getSQLContext().insert(item);

        item = new Result(UUID.randomUUID().toString());
        getSQLContext().insert(item);

        RPCResult result = new RPCResult();
        RPCResultMeta resultMeta = new RPCResultMeta();
        resultMeta.success = true;
        result.meta = resultMeta;
        result.tid = blockTid;
        result.action = ReflectionUtil.getTableName(Result.class);
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

        Result item = new Result(UUID.randomUUID().toString());
        item.jb_data = item.id;
        item.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;
        item.__TID = tid;
        item.__BLOCK_TID = String.valueOf(blockTid);
        getSQLContext().insert(item);

        item = new Result(UUID.randomUUID().toString());
        item.jb_data = item.id;
        getSQLContext().insert(item);

        RPCResult result = new RPCResult();
        RPCResultMeta resultMeta = new RPCResultMeta();
        resultMeta.success = true;
        result.meta = resultMeta;
        result.tid = blockTid;
        result.action = ReflectionUtil.getTableName(Result.class);
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
        Collection<Result> results = getSQLContext().select("select * from " + ReflectionUtil.getTableName(Result.class), new String[0], Result.class); // getDaoSession().getTrackingDao().queryBuilder().list();
        Assert.assertEquals(results.size(), 2);
        results = getSQLContext().select("select * from " + ReflectionUtil.getTableName(Result.class) + " where id = ?", new String[] { item.id }, Result.class); // getDaoSession().getTrackingDao().queryBuilder().where(TrackingDao.Properties.Id.eq(tracking2.getId())).list();
        Result t = results.toArray(new Result[0])[0];
        Assert.assertEquals(t.id, item.id);
    }

    @Test
    public void attachmentsFromTest() throws IOException {
        String LINK = UUID.randomUUID().toString();
        String tid = UUID.randomUUID().toString();
        int blockTid = 0;

        BasicCredential credentials = getCredentials();
        SimpleFileManager fileManager = new SimpleFileManager(getContext().getFilesDir(), credentials);
        fileManager.deleteFolder();

        fileManager.writeBytes("file1.tmp", "attachmentsToTest".getBytes());

        Attachment attachment = new Attachment();
        attachment.id = LINK;
        attachment.c_name = "file1.tmp";
        attachment.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;
        attachment.__TID = tid;
        attachment.__BLOCK_TID = String.valueOf(blockTid);

        getSQLContext().insert(attachment);

        RPCResult result = new RPCResult();
        RPCResultMeta resultMeta = new RPCResultMeta();
        resultMeta.success = true;
        result.meta = resultMeta;
        result.tid = blockTid;
        result.action = ReflectionUtil.getTableName(Attachment.class);
        result.method = "Query";
        RPCRecords records = new RPCRecords();
        records.total = 1;
        JsonObject[] objects = new JsonObject[1];

        JsonObject object = new JsonObject();
        object.addProperty("id", LINK);
        object.addProperty("c_name", "file1.tmp");

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
        String txt = new String(fileManager.readPath("file1.tmp"));
        Assert.assertEquals(txt, "attachmentsToTest");
    }

    static class MyServerSidePackageUtil extends ServerSidePackage {

    }
}