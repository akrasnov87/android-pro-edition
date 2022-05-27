/*package ru.mobnius.cic.data.manager.synchronization;

import android.content.Context;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import ru.mobnius.core.data.DbOperationType;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.synchronization.Entity;
import ru.mobnius.core.data.synchronization.FinishStatus;
import ru.mobnius.core.data.synchronization.MultipartUtility;
import ru.mobnius.core.utils.DateUtil;
import ru.mobnius.core.utils.SyncUtil;
import ru.mobnius.cic.ManagerGenerate;
import ru.mobnius.cic.data.storage.models.DaoSession;
import ru.mobnius.cic.data.storage.models.Tracking;
import ru.mobnius.cic.data.storage.models.TrackingDao;

public class ServiceSynchronizationTest extends ManagerGenerate {

    private ArrayList<Tracking> trackings;

    private ServiceSynchronizationTest.MySynchronization synchronization;

    @Before
    public void setUp(){
        synchronization = new MySynchronization(getContext(), getDaoSession(), getCredentials());
    }

    @After
    public void tearDown() {
        getDaoSession().getTrackingDao().deleteAll();
    }

    void generateData(){
        trackings = new ArrayList<>();
        for(int i =0; i < 10; i++) {
            Tracking tracking = new Tracking();
            tracking.d_date = DateUtil.convertDateToString(new Date());
            tracking.n_longitude = i* 10;
            tracking.n_latitude = i* 10;
            tracking.c_network_status = "LTE";
            tracking.fn_user = 1;
            tracking.setObjectOperationType(DbOperationType.CREATED);
            trackings.add(tracking);
            getDaoSession().getTrackingDao().insert(tracking);
        }
    }

    @Test
    public void fullCycle() throws Exception {
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

        Assert.assertNotEquals(synchronization.getFinishStatus(), FinishStatus.FAIL);

        // тут повторно отправляем
        for(Tracking tracking : trackings){
            getDaoSession().getTrackingDao().insert(tracking);
        }

        SyncUtil.updateTid(synchronization, TrackingDao.TABLENAME, tid);
        bytes = synchronization.generatePackage(tid, TrackingDao.TABLENAME);
        results = (byte[]) synchronization.sendBytes(tid, bytes);
        array = new String[1];
        array[0] = tid;
        synchronization.processingPackage(array, results);
        length = synchronization.getRecords(TrackingDao.TABLENAME, tid).toArray().length;
        Assert.assertEquals(length, 10);

        Assert.assertEquals(synchronization.getFinishStatus(), FinishStatus.FAIL);

        if(synchronization.getFinishStatus() == FinishStatus.FAIL){
            synchronization.oneOnlyMode = true;
            synchronization.changeFinishStatus(FinishStatus.NONE);
            SyncUtil.updateTid(synchronization, TrackingDao.TABLENAME, tid);
            bytes = synchronization.generatePackage(tid, TrackingDao.TABLENAME);
            results = (byte[]) synchronization.sendBytes(tid, bytes);
            array = new String[1];
            array[0] = tid;
            synchronization.processingPackage(array, results);
            length = synchronization.getRecords(TrackingDao.TABLENAME, tid).toArray().length;
            Assert.assertEquals(length, 0);
        }

        Assert.assertNotEquals(synchronization.getFinishStatus(), FinishStatus.FAIL);

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
            }catch (Exception exc){
                return  null;
            }
        }
    }
}*/