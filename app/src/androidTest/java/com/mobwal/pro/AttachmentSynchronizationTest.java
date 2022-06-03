package com.mobwal.pro;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.UUID;

import com.mobwal.android.library.FileManager;
import com.mobwal.android.library.authorization.credential.BasicCredential;

import com.mobwal.android.library.data.sync.EntityAttachment;
import com.mobwal.android.library.data.sync.MultipartUtility;
import com.mobwal.android.library.util.PackageReadUtils;

import static org.junit.Assert.assertTrue;
import com.mobwal.pro.models.db.Attachment;

public class AttachmentSynchronizationTest extends DbGenerate {
    private AttachmentSynchronizationTest.MySynchronization synchronization;

    @Before
    public void setUp() {
        getSQLContext().exec("DELETE FROM " + Attachment.Meta.table, new Object[0]);

        synchronization = new MySynchronization(getSQLContext(), getFileManager(), DbGenerate.getCredentials());
    }

    @After
    public void tearDown() {
        getSQLContext().trash();
        getFileManager().clearUserFolder();
    }

    @Test
    public void query() throws IOException {
        byte[] bytes = synchronization.generatePackage(synchronization.fileTid, (Object) null);
        byte[] results = (byte[]) synchronization.sendBytes(synchronization.fileTid, bytes);
        PackageReadUtils utils = new PackageReadUtils(results, synchronization.isZip());
        synchronization.onProcessingPackage(utils, synchronization.fileTid);

        Object[] array = synchronization.getRecords(Attachment.Meta.table, "").toArray();
        for(Object o : array) {
            Attachment attachment = (Attachment)o;
            assertTrue(getFileManager().exists(FileManager.ATTACHMENTS, attachment.c_path));
        }
    }

    public static class MySynchronization extends ManualSynchronization {
        private final BasicCredential mCredentials;
        public MySynchronization(WalkerSQLContext context, FileManager fileManager, BasicCredential credentials) {
            super(context, fileManager, false);
            fileTid = UUID.randomUUID().toString();
            addEntity(new EntityAttachment(Attachment.Meta.table, true, true).setParam(getUserID(), "1000.0.0.0").setUseCFunction().setTid(fileTid));
            //addEntity(new EntityAttachment(AttachmentsDao.TABLENAME, true, true).setParam(getUserID(), "1000.0.0.0").setUseCFunction().setTid(fileTid));
            mCredentials = credentials;
        }

        @Override
        protected Object sendBytes(String tid, byte[] bytes) {
            super.sendBytes(tid, bytes);

            try {
                MultipartUtility multipartUtility = new MultipartUtility(DbGenerate.getBaseUrl() + "/synchronization/" + PreferencesManager.SYNC_PROTOCOL_v2, mCredentials);
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