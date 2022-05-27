/*package ru.mobnius.cic.data.manager.synchronization;

import android.content.Context;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

import ru.mobnius.core.data.DbOperationType;
import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.rpc.FilterItem;
import ru.mobnius.core.data.synchronization.EntityAttachment;
import ru.mobnius.core.data.synchronization.FileTransferWebSocketSynchronization;
import ru.mobnius.core.data.synchronization.MultipartUtility;
import ru.mobnius.core.data.synchronization.utils.FullServerSidePackage;
import ru.mobnius.core.utils.DateUtil;
import ru.mobnius.core.utils.PackageReadUtils;
import ru.mobnius.core.utils.SyncUtil;
import ru.mobnius.cic.ManagerGenerate;
import ru.mobnius.cic.data.manager.DataManager;
import ru.mobnius.cic.data.storage.models.DaoSession;
import ru.mobnius.cic.data.storage.models.Files;
import ru.mobnius.cic.data.storage.models.FilesDao;

public class DeleteSynchronizationTest extends ManagerGenerate {
    private DataManager dataManager;
    private DeleteSynchronizationTest.MySynchronization synchronization;
    private String fileId = "";

    @Before
    public void setUp() {
        fileId = UUID.randomUUID().toString();
        synchronization = new DeleteSynchronizationTest.MySynchronization(getContext(), getDaoSession(), getFileManager(), getCredentials(), getBaseUrl());
        dataManager = DataManager.createInstance(getDaoSession());
        synchronization.initEntities();
    }

    @After
    public void tearDown() {
        getDaoSession().getFilesDao().deleteAll();
    }

    @Test
    public void deleteFile() throws IOException {
        Files file = new Files();
        file.id = fileId;
        file.c_name = "readme.txt";

        byte[] fileBytes = "Hello World!!!".getBytes();

        FileManager fileManager = synchronization.getFileManager();
        fileManager.writeBytes(FileManager.FILES, file.c_name, fileBytes);

        file.c_mime = "text/plain";
        file.c_extension = ".txt";
        file.folder = FileManager.FILES;
        file.d_date = DateUtil.convertDateToString(new Date());
        file.objectOperationType = DbOperationType.CREATED;

        getDaoSession().getFilesDao().insert(file);
        SyncUtil.updateTid(synchronization, FilesDao.TABLENAME, synchronization.fileTid);

        byte[] bytes = synchronization.generatePackage(synchronization.fileTid, (Object) null);
        byte[] results = (byte[]) synchronization.sendBytes(synchronization.fileTid, bytes);
        PackageReadUtils utils = new PackageReadUtils(results, synchronization.isZip());
        synchronization.onProcessingPackage(utils, synchronization.fileTid);
        Object[] array = synchronization.getRecords(FilesDao.TABLENAME, "").toArray();

        Files resultFile = getFile(array, file.c_name);

        Assert.assertNotNull(resultFile);
        utils.destroy();
        getDaoSession().getFilesDao().detachAll();

        dataManager.removeFile(file.id);

        SyncUtil.updateTid(synchronization, FilesDao.TABLENAME, synchronization.fileTid);
        getDaoSession().getFilesDao().detachAll();
        bytes = synchronization.generatePackage(synchronization.fileTid, (Object) null);
        results = (byte[]) synchronization.sendBytes(synchronization.fileTid, bytes);
        utils = new PackageReadUtils(results, synchronization.isZip());
        synchronization.onProcessingPackage(utils, synchronization.fileTid);
        array = synchronization.getRecords(FilesDao.TABLENAME, "").toArray();

        resultFile = getFile(array, file.c_name);
        Assert.assertNull(resultFile);
    }

    private Files getFile(Object[] array, String fileName) {
        Files resultFile = null;
        for (Object obj : array) {
            if (obj instanceof Files) {
                resultFile = (Files) obj;
                if (resultFile.c_name.equals(fileName)) {
                    break;
                } else {
                    resultFile = null;
                }
            }
        }
        return resultFile;
    }

    public class MySynchronization extends FileTransferWebSocketSynchronization {

        private final BasicCredentials mCredentials;
        private final String mBaseUrl;

        public MySynchronization(Context context, DaoSession daoSession, FileManager fileManager, BasicCredentials credentials, String baseUrl) {
            super(context, daoSession, "test", fileManager, false);
            useAttachments = true;
            oneOnlyMode = true;
            serverSidePackage = new FullServerSidePackage();
            mCredentials = credentials;
            mBaseUrl = baseUrl;
        }

        @Override
        protected Object sendBytes(String tid, byte[] bytes) {
            super.sendBytes(tid, bytes);

            try {
                MultipartUtility multipartUtility = new MultipartUtility(mBaseUrl + "/synchronization/" + PreferencesManager.SYNC_PROTOCOL_v2, mCredentials);
                multipartUtility.addFilePart("synchronization", bytes);
                return multipartUtility.finish();
            } catch (Exception exc) {
                return null;
            }
        }

        @Override
        protected void initEntities() {
            fileTid = UUID.randomUUID().toString();
            addEntity(new EntityAttachment(FilesDao.TABLENAME, true, true)
                    .setSelect("id", "c_name", "d_date", "c_mime", "c_extension", "n_size", "'files' as folder", "ba_data")
                    .setFilter(new FilterItem("id", fileId))
                    .setTid(fileTid)
                    .setParam(null, "null"));
        }
    }
}
*/