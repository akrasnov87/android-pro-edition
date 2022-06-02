package com.mobwal.pro.data.manager.synchronization;

import com.mobwal.pro.ServiceSynchronization;
import com.mobwal.pro.WalkerSQLContext;
import com.mobwal.pro.data.DbGenerate;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.UUID;

import ru.mobnius.core.data.DbOperationType;
import ru.mobnius.core.data.configuration.PreferencesManager;
import com.mobwal.android.library.authorization.credential.BasicCredential;

import com.mobwal.pro.data.Entity;
import com.mobwal.pro.data.MultipartUtility;
import com.mobwal.pro.models.db.Result;
import com.mobwal.pro.utilits.SyncUtil;

public class BaseSynchronizationTest extends DbGenerate {
    private MySynchronization synchronization;

    private ArrayList<Result> mResults;

    @Before
    public void setUp() {
        synchronization = new MySynchronization(getSQLContext(), getCredentials());

        getSQLContext().exec("DELETE FROM " + Result.Meta.table, new Object[0]);
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
        synchronization.addEntity(new Entity(Result.Meta.table).setTid(tid));

        generateData();

        int length = synchronization.getRecords(Result.Meta.table, "").toArray().length;
        Assert.assertEquals(length, 10);

        SyncUtil.updateTid(synchronization, Result.Meta.table, tid);

        length = synchronization.getRecords(Result.Meta.table, tid).toArray().length;
        Assert.assertEquals(length, 10);
        boolean reset = SyncUtil.resetTid(synchronization);
        Assert.assertTrue(reset);
        boolean update = SyncUtil.updateTid(synchronization, Result.Meta.table, tid);
        Assert.assertTrue(update);
        length = synchronization.getRecords(Result.Meta.table, tid).toArray().length;
        Assert.assertEquals(length, 10);

        byte[] bytes = synchronization.generatePackage(tid, Result.Meta.table);
        byte[] results = (byte[]) synchronization.sendBytes(tid, bytes);
        String[] array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);
        length = synchronization.getRecords(Result.Meta.table, tid).toArray().length;
        Assert.assertEquals(length, 0);

        synchronization.destroy();
    }

    @Test
    public void duplicateRecords() throws Exception {
        String tid = UUID.randomUUID().toString();
        synchronization.addEntity(new Entity(Result.Meta.table).setTid(tid));
        generateData();

        SyncUtil.updateTid(synchronization, Result.Meta.table, tid);
        byte[] bytes = synchronization.generatePackage(tid, Result.Meta.table);
        byte[] results = (byte[]) synchronization.sendBytes(tid, bytes);
        String[] array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);
        int length = synchronization.getRecords(Result.Meta.table, tid).toArray().length;
        Assert.assertEquals(length, 0);
        getSQLContext().exec("delete from " + Result.Meta.table, new Object[0]);

        // тут повторно отправляем
        for (Result item : mResults) {
            getSQLContext().insert(item);
        }
        // принудительно указывается, что нужно передать данные в другом режиме
        synchronization.oneOnlyMode = true;
        SyncUtil.updateTid(synchronization, Result.Meta.table, tid);
        bytes = synchronization.generatePackage(tid, Result.Meta.table);
        results = (byte[]) synchronization.sendBytes(tid, bytes);
        array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);
        length = synchronization.getRecords(Result.Meta.table, tid).toArray().length;
        Assert.assertEquals(length, 0);
    }

    @Test
    public void twoTable() throws Exception {
        String tid = UUID.randomUUID().toString();
        synchronization.addEntity(new Entity(Result.Meta.table).setTid(tid).setSchema("dbo"));

        generateData();

        SyncUtil.resetTid(synchronization);
        boolean b = SyncUtil.updateTid(synchronization, tid);
        Assert.assertTrue(b);

        // передача первого пакета
        byte[] bytes = synchronization.generatePackage(tid, Result.Meta.table);
        byte[] results = (byte[]) synchronization.sendBytes(tid, bytes);
        String[] array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);

        // передача второго пакета
        /*bytes = synchronization.generatePackage(tid, AuditsDao.TABLENAME);
        results = (byte[]) synchronization.sendBytes(tid, bytes);
        array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);*/

        int length = synchronization.getRecords(Result.Meta.table, "").toArray().length;
        Assert.assertEquals(length, 0);
        //length = synchronization.getRecords(AuditsDao.TABLENAME, "").toArray().length;
        //Assert.assertEquals(length, 2);//потому что в методе onProcessingPackage класса ServiceSynchronization добавилась строчка
        // AuditManager.getInstance().write(String.valueOf(utils.getLength()), AuditListeners.TRAFFIC_INPUT, OnAuditListeners.Level.HIGH);
        // которая вызывается дважды - по одному разу для каждого пакета

        synchronization.destroy();
    }

    static class MySynchronization extends ServiceSynchronization {

        private final BasicCredential mCredentials;

        public MySynchronization(WalkerSQLContext context, BasicCredential credentials) {
            super(context, false);

            mCredentials = credentials;
        }

        @Override
        protected Object sendBytes(String tid, byte[] bytes) {
            super.sendBytes(tid, bytes);

            try {
                MultipartUtility multipartUtility = new MultipartUtility(getBaseUrl() + "/synchronization/" + PreferencesManager.SYNC_PROTOCOL_v2, mCredentials);
                multipartUtility.addFilePart("synchronization", bytes);
                return multipartUtility.finish();
            } catch (Exception exc) {
                return null;
            }
        }
    }
}