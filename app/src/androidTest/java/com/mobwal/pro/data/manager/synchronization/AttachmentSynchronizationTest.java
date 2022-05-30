/*package ru.mobnius.cic.data.manager.synchronization;

import android.content.Context;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.utils.LongUtil;
import ru.mobnius.core.utils.PackageReadUtils;

import static org.junit.Assert.assertTrue;

public class AttachmentSynchronizationTest extends DbGenerate {
    private AttachmentSynchronizationTest.MySynchronization synchronization;

    @Before
    public void setUp() {
        synchronization = new MySynchronization(getContext(), getDaoSession(), getFileManager(), getCredentials());
    }

    @After
    public void tearDown() {
        getDaoSession().getFilesDao().deleteAll();
        getDaoSession().getAttachmentsDao().deleteAll();
        getFileManager().clearUserFolder();
    }

    @Test
    public void query() throws IOException {
        byte[] bytes = synchronization.generatePackage(synchronization.fileTid, (Object) null);
        byte[] results = (byte[]) synchronization.sendBytes(synchronization.fileTid, bytes);
        PackageReadUtils utils = new PackageReadUtils(results, synchronization.isZip());
        synchronization.onProcessingPackage(utils, synchronization.fileTid);

        Object[] array = synchronization.getRecords(AttachmentsDao.TABLENAME, "").toArray();
        for(Object o : array) {
            Attachments attachment = (Attachments)o;
            assertTrue(getFileManager().exists(FileManager.ATTACHMENTS, attachment.c_name));
        }
    }

    public static class MySynchronization extends ManualSynchronization {
        private final BasicCredentials mCredentials;
        public MySynchronization(Context context, DaoSession daoSession, FileManager fileManager, BasicCredentials credentials) {
            super(context, daoSession, fileManager, false);
            fileTid = UUID.randomUUID().toString();
            addEntity(new EntityAttachment(FilesDao.TABLENAME, true, true).setParam(getUserID(), "1000.0.0.0").setUseCFunction().setTid(fileTid));
            addEntity(new EntityAttachment(AttachmentsDao.TABLENAME, true, true).setParam(getUserID(), "1000.0.0.0").setUseCFunction().setTid(fileTid));
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
}*/