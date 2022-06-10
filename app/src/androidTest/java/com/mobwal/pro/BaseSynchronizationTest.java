package com.mobwal.pro;

import com.mobwal.android.library.SimpleFileManager;
import com.mobwal.android.library.data.DbOperationType;
import com.mobwal.android.library.util.ReflectionUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.UUID;

import com.mobwal.android.library.authorization.credential.BasicCredential;

import com.mobwal.android.library.data.sync.Entity;
import com.mobwal.android.library.data.sync.MultipartUtility;
import com.mobwal.pro.models.db.Result;
import com.mobwal.android.library.util.SyncUtil;

public class BaseSynchronizationTest extends DbGenerate {
    private MySynchronization synchronization;

    private ArrayList<Result> mResults;

    @Before
    public void setUp() {
        synchronization = new MySynchronization(getSQLContext(), getFileManager(), DbGenerate.getCredentials());

        getSQLContext().exec("DELETE FROM " + ReflectionUtil.getTableMetaData(Result.class).name(), new Object[0]);
    }

    @After
    public void tearDown() {
        getSQLContext().trash();
    }

    void generateData() {
        mResults = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Result item = new Result();
            item.jb_data = "{\"idx\": "+i+"}";
            item.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;
            mResults.add(item);
            getSQLContext().insert(item);
        }
    }

    @Test
    public void oneTable() throws Exception {
        String tid = UUID.randomUUID().toString();
        synchronization.addEntity(new Entity(Result.class).setTid(tid));

        generateData();

        int length = synchronization.getRecords(ReflectionUtil.getTableName(Result.class), "").toArray().length;
        Assert.assertEquals(length, 10);

        SyncUtil.updateTid(synchronization, ReflectionUtil.getTableName(Result.class), tid);

        length = synchronization.getRecords(ReflectionUtil.getTableName(Result.class), tid).toArray().length;
        Assert.assertEquals(length, 10);
        boolean reset = SyncUtil.resetTid(synchronization);
        Assert.assertTrue(reset);
        boolean update = SyncUtil.updateTid(synchronization, ReflectionUtil.getTableName(Result.class), tid);
        Assert.assertTrue(update);
        length = synchronization.getRecords(ReflectionUtil.getTableName(Result.class), tid).toArray().length;
        Assert.assertEquals(length, 10);

        byte[] bytes = synchronization.generatePackage(tid, ReflectionUtil.getTableName(Result.class));
        byte[] results = (byte[]) synchronization.sendBytes(tid, bytes);
        String[] array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);
        length = synchronization.getRecords(ReflectionUtil.getTableName(Result.class), tid).toArray().length;
        Assert.assertEquals(length, 0);

        synchronization.destroy();
    }

    @Test
    public void duplicateRecords() throws Exception {
        String tid = UUID.randomUUID().toString();
        synchronization.addEntity(new Entity(Result.class).setTid(tid));
        generateData();

        SyncUtil.updateTid(synchronization, ReflectionUtil.getTableName(Result.class), tid);
        byte[] bytes = synchronization.generatePackage(tid, ReflectionUtil.getTableName(Result.class));
        byte[] results = (byte[]) synchronization.sendBytes(tid, bytes);
        String[] array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);
        int length = synchronization.getRecords(ReflectionUtil.getTableName(Result.class), tid).toArray().length;
        Assert.assertEquals(length, 0);
        getSQLContext().exec("delete from " + ReflectionUtil.getTableName(Result.class), new Object[0]);

        // тут повторно отправляем
        for (Result item : mResults) {
            getSQLContext().insert(item);
        }
        // принудительно указывается, что нужно передать данные в другом режиме
        synchronization.oneOnlyMode = true;
        SyncUtil.updateTid(synchronization, ReflectionUtil.getTableName(Result.class), tid);
        bytes = synchronization.generatePackage(tid, ReflectionUtil.getTableName(Result.class));
        results = (byte[]) synchronization.sendBytes(tid, bytes);
        array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);
        length = synchronization.getRecords(ReflectionUtil.getTableName(Result.class), tid).toArray().length;
        Assert.assertEquals(length, 0);
    }

    // TODO: обновить тест на обработку двух таблиц
    @Test
    public void twoTable() throws Exception {
        String tid = UUID.randomUUID().toString();
        // TODO: переделать на аудит
        synchronization.addEntity(new Entity(Result.class).setParam("1.0.0.0").setClearable().setTid(tid));

        generateData();

        SyncUtil.resetTid(synchronization);
        boolean b = SyncUtil.updateTid(synchronization, tid);
        Assert.assertTrue(b);

        // передача первого пакета
        byte[] bytes = synchronization.generatePackage(tid, ReflectionUtil.getTableName(Result.class));
        byte[] results = (byte[]) synchronization.sendBytes(tid, bytes);
        String[] array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);

        int length = synchronization.getRecords(ReflectionUtil.getTableName(Result.class), "").toArray().length;
        Assert.assertEquals(length, 0);

        synchronization.destroy();
    }

    public static class MySynchronization extends ManualSynchronization {
        private final BasicCredential mCredentials;
        private static long DEFAULT_USER_ID = 4;

        public MySynchronization(WalkerSQLContext context, SimpleFileManager fileManager, BasicCredential credentials) {
            super(context, fileManager, false);
            mCredentials = credentials;
        }

        @Override
        protected Object sendBytes(String tid, byte[] bytes) {
            super.sendBytes(tid, bytes);

            try {
                MultipartUtility multipartUtility = new MultipartUtility(DbGenerate.getBaseUrl() + "/synchronization/v2", mCredentials);
                multipartUtility.addFilePart("synchronization", bytes);
                return multipartUtility.finish();
            }catch (Exception exc){
                return  null;
            }
        }

        @Override
        public long getUserID() {
            return DEFAULT_USER_ID;
        }
    }
}