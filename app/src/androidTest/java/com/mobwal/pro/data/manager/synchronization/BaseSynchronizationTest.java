/*package com.mobwal.pro.data.manager.synchronization;

import android.content.Context;

import com.mobwal.pro.data.DbGenerate;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import ru.mobnius.core.data.DbOperationType;
import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.utils.DateUtil;
import com.mobwal.pro.utilits.SyncUtil;

public class BaseSynchronizationTest extends DbGenerate {
    private MySynchronization synchronization;

    private ArrayList<Tracking> trackings;

    @Before
    public void setUp() {
        synchronization = new MySynchronization(getContext(), getDaoSession(), getCredentials());
    }

    void generateData() {
        trackings = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Tracking tracking = new Tracking();
            tracking.fn_user = 1;
            tracking.c_network_status = "LTE";
            tracking.n_latitude = i * 10.0;
            tracking.n_longitude = i * 10.0;
            tracking.d_date = DateUtil.convertDateToString(new Date());
            tracking.setObjectOperationType(DbOperationType.CREATED);
            trackings.add(tracking);
            getDaoSession().getTrackingDao().insert(tracking);
        }
        for (int i = 0; i < 10; i++) {
            Audits audit = new Audits();
            audit.d_date = DateUtil.convertDateToString(new Date());
            audit.fn_user = Long.parseLong(String.valueOf(GlobalSettings.DEFAULT_USER_ID));
            audit.c_type = AuditListeners.ON_AUTH;
            audit.c_data = "Номер " + i;
            audit.tid = UUID.randomUUID().toString();
            audit.setObjectOperationType(DbOperationType.CREATED);
            getDaoSession().getAuditsDao().insert(audit);
        }
    }

    @Test
    public void oneTable() throws Exception {
        String tid = UUID.randomUUID().toString();
        synchronization.addEntity(new Entity(TrackingDao.TABLENAME).setTid(tid));

        generateData();

        int length = synchronization.getRecords(TrackingDao.TABLENAME, "").toArray().length;
        Assert.assertEquals(length, 10);

        SyncUtil.updateTid(synchronization, TrackingDao.TABLENAME, tid);

        length = synchronization.getRecords(TrackingDao.TABLENAME, tid).toArray().length;
        Assert.assertEquals(length, 10);
        boolean reset = resetTid(synchronization);
        Assert.assertTrue(reset);
        boolean update = SyncUtil.updateTid(synchronization, TrackingDao.TABLENAME, tid);
        Assert.assertTrue(update);
        length = synchronization.getRecords(TrackingDao.TABLENAME, tid).toArray().length;
        Assert.assertEquals(length, 10);

        byte[] bytes = synchronization.generatePackage(tid, TrackingDao.TABLENAME);
        byte[] results = (byte[]) synchronization.sendBytes(tid, bytes);
        String[] array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);
        length = synchronization.getRecords(TrackingDao.TABLENAME, tid).toArray().length;
        Assert.assertEquals(length, 0);

        synchronization.destroy();
    }

    @Test
    public void duplicateRecords() throws Exception {
        String tid = UUID.randomUUID().toString();
        synchronization.addEntity(new Entity(TrackingDao.TABLENAME).setTid(tid));
        generateData();

        SyncUtil.updateTid(synchronization, TrackingDao.TABLENAME, tid);
        byte[] bytes = synchronization.generatePackage(tid, TrackingDao.TABLENAME);
        byte[] results = (byte[]) synchronization.sendBytes(tid, bytes);
        String[] array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);
        int length = synchronization.getRecords(TrackingDao.TABLENAME, tid).toArray().length;
        Assert.assertEquals(length, 0);
        getDaoSession().getTrackingDao().deleteAll();

        // тут повторно отправляем
        for (Tracking tracking : trackings) {
            getDaoSession().getTrackingDao().insert(tracking);
        }
        // принудительно указывается, что нужно передать данные в другом режиме
        synchronization.oneOnlyMode = true;
        SyncUtil.updateTid(synchronization, TrackingDao.TABLENAME, tid);
        bytes = synchronization.generatePackage(tid, TrackingDao.TABLENAME);
        results = (byte[]) synchronization.sendBytes(tid, bytes);
        array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);
        length = synchronization.getRecords(TrackingDao.TABLENAME, tid).toArray().length;
        Assert.assertEquals(length, 0);
    }

    @Test
    public void twoTable() throws Exception {
        String tid = UUID.randomUUID().toString();
        synchronization.addEntity(new Entity(TrackingDao.TABLENAME).setTid(tid));
        synchronization.addEntity(new Entity(AuditsDao.TABLENAME).setTid(tid));

        generateData();

        resetTid(synchronization);
        boolean b = SyncUtil.updateTid(synchronization, tid);
        Assert.assertTrue(b);

        // передача первого пакета
        byte[] bytes = synchronization.generatePackage(tid, TrackingDao.TABLENAME);
        byte[] results = (byte[]) synchronization.sendBytes(tid, bytes);
        String[] array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);

        // передача второго пакета
        bytes = synchronization.generatePackage(tid, AuditsDao.TABLENAME);
        results = (byte[]) synchronization.sendBytes(tid, bytes);
        array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);

        int length = synchronization.getRecords(TrackingDao.TABLENAME, "").toArray().length;
        Assert.assertEquals(length, 0);
        length = synchronization.getRecords(AuditsDao.TABLENAME, "").toArray().length;
        Assert.assertEquals(length, 2);//потому что в методе onProcessingPackage класса ServiceSynchronization добавилась строчка
        // AuditManager.getInstance().write(String.valueOf(utils.getLength()), AuditListeners.TRAFFIC_INPUT, OnAuditListeners.Level.HIGH);
        // которая вызывается дважды - по одному разу для каждого пакета

        synchronization.destroy();
    }

    static class MySynchronization extends ServiceSynchronization {

        private final BasicCredentials mCredentials;

        public MySynchronization(Context context, DaoSession daoSession, BasicCredentials credentials) {
            super(context, daoSession, false);

            mCredentials = credentials;
        }

        @Override
        protected Object sendBytes(String tid, byte[] bytes) {
            super.sendBytes(tid, bytes);

            try {
                MultipartUtility multipartUtility = new MultipartUtility(ManagerGenerate.getBaseUrl() + "/synchronization/" + PreferencesManager.SYNC_PROTOCOL_v2, mCredentials);
                multipartUtility.addFilePart("synchronization", bytes);
                return multipartUtility.finish();
            } catch (Exception exc) {
                return null;
            }
        }
    }
}
*/