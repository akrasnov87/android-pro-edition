package com.mobwal.pro;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import com.mobwal.android.library.SimpleFileManager;
import com.mobwal.android.library.authorization.credential.BasicCredential;

import com.mobwal.android.library.data.DbOperationType;
import com.mobwal.android.library.data.sync.EntityAttachment;
import com.mobwal.android.library.data.sync.MultipartUtility;
import com.mobwal.android.library.util.PackageReadUtils;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.mobwal.android.library.util.ReflectionUtil;
import com.mobwal.android.library.util.SyncUtil;
import com.mobwal.pro.models.db.Attachment;

public class AttachmentSynchronizationTest extends DbGenerate {

    private AttachmentSynchronizationTest.MySynchronization synchronization;

    @Before
    public void setUp() {
        // создаем файлы для тестирования
        for(int i = 0; i < 2; i++) {
            byte[] bytes = ("file number " + i).getBytes();
            saveFile("file" + i + ".tmp", bytes);
        }

        synchronization = new MySynchronization(getSQLContext(), getFileManager(), getCredentials());
    }

    @Test
    public void query() throws IOException {
        byte[] bytes = synchronization.generatePackage(synchronization.fileTid, (Object) null);
        byte[] results = (byte[]) synchronization.sendBytes(synchronization.fileTid, bytes);
        PackageReadUtils utils = new PackageReadUtils(results, synchronization.isZip());
        synchronization.onProcessingPackage(utils, synchronization.fileTid);

        Object[] array = synchronization.getRecords(ReflectionUtil.getTableMetaData(Attachment.class).name(), "").toArray();

        Assert.assertTrue(array.length > 0);

        for(Object o : array) {
            Attachment attachment = (Attachment)o;
            assertFalse(getFileManager().exists(attachment.c_name));
        }
    }

    @After
    public void tearDown() {
        destroy();
    }

    public static class MySynchronization extends ManualSynchronization {
        private final BasicCredential mCredentials;
        public MySynchronization(WalkerSQLContext context, SimpleFileManager fileManager, BasicCredential credentials) {
            super(context, fileManager, false);
            fileTid = UUID.randomUUID().toString();

            addEntity(new EntityAttachment(Attachment.class).setParam("").setTid(fileTid));
            mCredentials = credentials;

            SyncUtil.updateTid(this, ReflectionUtil.getTableName(Attachment.class), fileTid);
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