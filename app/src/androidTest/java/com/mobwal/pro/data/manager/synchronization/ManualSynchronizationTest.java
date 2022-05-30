/*package ru.mobnius.cic.data.manager.synchronization;

import android.content.Context;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import ru.mobnius.cic.data.storage.models.Points;
import ru.mobnius.cic.data.storage.models.Results;
import ru.mobnius.cic.data.storage.models.RouteStatusesDao;
import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.synchronization.MultipartUtility;
import ru.mobnius.core.utils.LocationUtil;
import ru.mobnius.core.utils.LongUtil;
import ru.mobnius.core.utils.PackageReadUtils;
import com.mobwal.pro.utilits.SyncUtil;
import ru.mobnius.cic.ManagerGenerate;
import ru.mobnius.cic.data.storage.models.DaoSession;

import static org.junit.Assert.assertEquals;

public class ManualSynchronizationTest extends ManagerGenerate {
    private ManualSynchronizationTest.MySynchronization synchronization;

    private String mResultId;
    private String mPointId;

    @Before
    public void setUp() {

        Points point = new Points();
        point.id = mPointId = UUID.randomUUID().toString();
        getDaoSession().getPointsDao().insert(point);

        Results result = new Results();
        result.fn_point = mPointId;
        result.id = mResultId = UUID.randomUUID().toString();
        getDaoSession().getResultsDao().insert(result);

        synchronization = new MySynchronization(getContext(), getDaoSession(), getFileManager(), getCredentials());
        synchronization.initEntities();

        // без этого фильтрации не будет работать
        // TODO 09/01/2020 нужно добавить проверку в метод start у синхронизации на передачу идентификатора пользователя, что null не было
        synchronization.getEntity(getDaoSession().getAttachmentsDao().getTablename()).setParam("null", "1000.0.0.0");
        synchronization.getEntity(getDaoSession().getFilesDao().getTablename()).setParam("null", "1000.0.0.0");
    }

    @After
    public void tearDown() {
        getDaoSession().getFilesDao().deleteAll();
        getDaoSession().getAttachmentsDao().deleteAll();
        getDaoSession().getResultsDao().deleteAll();
        getDaoSession().getPointsDao().deleteAll();
    }

    @Test
    public void from() throws IOException {
        byte[] bytes = synchronization.generatePackage(synchronization.dictionaryTid, (Object) null);
        byte[] results = (byte[]) synchronization.sendBytes(synchronization.dictionaryTid, bytes);
        PackageReadUtils utils = new PackageReadUtils(results, synchronization.isZip());
        synchronization.onProcessingPackage(utils, synchronization.dictionaryTid);
        int length = synchronization.getRecords(RouteStatusesDao.TABLENAME, "").toArray().length;
        Assert.assertTrue(length > 0);
        synchronization.destroy();
        utils.destroy();
    }

    @Test
    public void attachments() throws IOException {
        FileManager fileManager = synchronization.getFileManager();
        try {
            fileManager.deleteFolder(FileManager.FILES);
        }catch (FileNotFoundException ignored){

        }

        // создаем записи о вложениях
        DaoSession session = getDaoSession();

        for(int i = 0; i < 2; i++) {
            byte[] bytes = ("file number " + i).getBytes();
            getDataManager().saveAttachment("file" + i + ".tmp", 1, mResultId, mPointId, UUID.randomUUID().toString(), "", LocationUtil.getLocation(0, 0), bytes);
        }

        boolean updateTid = SyncUtil.updateTid(synchronization, session.getAttachmentsDao().getTablename(), synchronization.fileTid);
        Assert.assertTrue(updateTid);

        updateTid = SyncUtil.updateTid(synchronization, session.getFilesDao().getTablename(), synchronization.fileTid);
        Assert.assertTrue(updateTid);

        byte[] bytes = synchronization.generatePackage(synchronization.fileTid, (Object) null);
        byte[] results = (byte[]) synchronization.sendBytes(synchronization.fileTid, bytes);

        try {
            fileManager.deleteFolder(FileManager.FILES);
        }catch (FileNotFoundException ignored) {

        }

        getDaoSession().getAttachmentsDao().deleteAll();
        getDaoSession().getFilesDao().deleteAll();

        PackageReadUtils utils = new PackageReadUtils(results, synchronization.isZip());
        synchronization.onProcessingPackage(utils, synchronization.fileTid);
        byte[] fileBytes = fileManager.readPath(FileManager.FILES, "file0.tmp");
        Assert.assertNotNull(fileBytes);
        assertEquals(new String(fileBytes), "file number 0");

        @SuppressWarnings("rawtypes") List records = synchronization.getRecords(session.getAttachmentsDao().getTablename(), "");
        Assert.assertTrue(records.size() >= 2);

        records = synchronization.getRecords(session.getFilesDao().getTablename(), "");
        Assert.assertTrue(records.size() >= 2);

        synchronization.destroy();
        try {
            fileManager.deleteFolder(FileManager.FILES);
        }catch (FileNotFoundException ignored){

        }
        utils.destroy();
    }

    public static class MySynchronization extends ManualSynchronization {
        private final BasicCredentials mCredentials;
        public MySynchronization(Context context, DaoSession daoSession, FileManager fileManager, BasicCredentials credentials) {
            super(context, daoSession, fileManager, false);
            dictionaryTid = UUID.randomUUID().toString();
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

        @Override
        public long getUserID() {
            return LongUtil.convertToLong(GlobalSettings.DEFAULT_USER_ID);
        }
    }
}
*/