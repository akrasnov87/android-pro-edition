package com.mobwal.pro;

import com.mobwal.android.library.SimpleFileManager;
import com.mobwal.android.library.data.DbOperationType;
import com.mobwal.android.library.util.ReflectionUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.mobwal.android.library.authorization.credential.BasicCredential;

import com.mobwal.android.library.data.sync.Entity;
import com.mobwal.android.library.data.sync.MultipartUtility;
import com.mobwal.pro.models.db.Audit;
import com.mobwal.pro.models.db.MobileDevice;
import com.mobwal.pro.models.db.Result;
import com.mobwal.android.library.util.SyncUtil;

public class BaseSynchronizationTest extends DbGenerate {
    private MySynchronization synchronization;

    private ArrayList<Audit> mAudits;

    @Before
    public void setUp() {

        mAudits = new ArrayList<>();

        for (int i = 0; i < 10; i++) {
            Audit item = new Audit();
            item.c_type = "INFO";
            item.c_session_id = UUID.randomUUID().toString();
            item.c_data = "audit_" + i;
            item.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;

            mAudits.add(item);
        }

        getSQLContext().insertMany(mAudits.toArray(new Audit[0]));

        ArrayList<MobileDevice> deviceList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            MobileDevice item = new MobileDevice();
            item.c_ip = "127.0.0.1";
            item.c_session_id = UUID.randomUUID().toString();
            item.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;

            deviceList.add(item);
        }

        getSQLContext().insertMany(deviceList.toArray(new MobileDevice[0]));

        synchronization = new MySynchronization(getSQLContext(), getFileManager(), getCredentials());
    }

    @Test
    public void oneTable() throws Exception {
        String tid = UUID.randomUUID().toString();
        synchronization.addEntity(new Entity(Audit.class).setMany().setClearable().setTid(tid));

        int length = synchronization.getRecords(ReflectionUtil.getTableName(Audit.class), "").toArray().length;
        Assert.assertEquals(length, 10);

        SyncUtil.updateTid(synchronization, ReflectionUtil.getTableName(Audit.class), tid);

        length = synchronization.getRecords(ReflectionUtil.getTableName(Audit.class), tid).toArray().length;
        Assert.assertEquals(length, 10);
        boolean reset = SyncUtil.resetTid(synchronization);
        Assert.assertTrue(reset);
        boolean update = SyncUtil.updateTid(synchronization, ReflectionUtil.getTableName(Audit.class), tid);
        Assert.assertTrue(update);
        length = synchronization.getRecords(ReflectionUtil.getTableName(Audit.class), tid).toArray().length;
        Assert.assertEquals(length, 10);

        byte[] bytes = synchronization.generatePackage(tid, ReflectionUtil.getTableName(Audit.class));
        byte[] results = (byte[]) synchronization.sendBytes(tid, bytes);
        String[] array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);
        length = synchronization.getRecords(ReflectionUtil.getTableName(Audit.class), tid).toArray().length;
        Assert.assertEquals(length, 0);

        synchronization.destroy();
    }

    @Test
    public void duplicateRecords() throws Exception {
        String tid = UUID.randomUUID().toString();
        synchronization.addEntity(new Entity(Audit.class).setTid(tid));

        SyncUtil.updateTid(synchronization, ReflectionUtil.getTableName(Audit.class), tid);
        byte[] bytes = synchronization.generatePackage(tid, ReflectionUtil.getTableName(Audit.class));
        byte[] results = (byte[]) synchronization.sendBytes(tid, bytes);
        String[] array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);
        int length = synchronization.getRecords(ReflectionUtil.getTableName(Audit.class), tid).toArray().length;
        Assert.assertEquals(length, 0);
        getSQLContext().exec("delete from " + ReflectionUtil.getTableName(Audit.class), new Object[0]);

        // тут повторно отправляем
        for (Audit item : mAudits) {
            getSQLContext().insert(item);
        }
        // принудительно указывается, что нужно передать данные в другом режиме
        synchronization.oneOnlyMode = true;
        SyncUtil.updateTid(synchronization, ReflectionUtil.getTableName(Audit.class), tid);
        bytes = synchronization.generatePackage(tid, ReflectionUtil.getTableName(Audit.class));
        results = (byte[]) synchronization.sendBytes(tid, bytes);
        array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);
        length = synchronization.getRecords(ReflectionUtil.getTableName(Audit.class), tid).toArray().length;
        Assert.assertEquals(length, 0);
    }

    // TODO: обновить тест на обработку двух таблиц
    @Test
    public void twoTable() throws Exception {
        String tid = UUID.randomUUID().toString();
        // TODO: переделать на аудит
        synchronization.addEntity(new Entity(Audit.class).setMany().setClearable().setTid(tid));
        synchronization.addEntity(new Entity(MobileDevice.class).setMany().setClearable().setTid(tid));

        SyncUtil.resetTid(synchronization);
        boolean b = SyncUtil.updateTid(synchronization, tid);
        Assert.assertTrue(b);

        // передача первого пакета
        byte[] bytes = synchronization.generatePackage(tid, ReflectionUtil.getTableName(Audit.class));
        byte[] results = (byte[]) synchronization.sendBytes(tid, bytes);
        String[] array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);

        // передача второго пакета
        bytes = synchronization.generatePackage(tid, ReflectionUtil.getTableName(MobileDevice.class));
        results = (byte[]) synchronization.sendBytes(tid, bytes);
        array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);

        int length = synchronization.getRecords(ReflectionUtil.getTableName(Audit.class), "").toArray().length;
        Assert.assertEquals(length, 0);

        length = synchronization.getRecords(ReflectionUtil.getTableName(MobileDevice.class), "").toArray().length;
        Assert.assertEquals(length, 0);

        synchronization.destroy();
    }

    @After
    public void tearDown() {
        destroy();
    }

    public static class MySynchronization extends ManualSynchronization {
        private final BasicCredential mCredentials;

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
            return getBasicUser().getUserId();
        }
    }
}