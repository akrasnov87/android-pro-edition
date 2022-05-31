package com.mobwal.pro.data.manager.synchronization;

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

import com.mobwal.pro.ManualSynchronization;
import com.mobwal.pro.WalkerSQLContext;
import com.mobwal.pro.data.DbGenerate;
import com.mobwal.pro.data.EntityAttachment;
import com.mobwal.pro.data.MultipartUtility;
import com.mobwal.pro.models.db.attachments;

public class AttachmentSynchronizationTest extends DbGenerate {
    private AttachmentSynchronizationTest.MySynchronization synchronization;

    @Before
    public void setUp() {
        getSQLContext().exec("DELETE FROM " + attachments.Meta.table, new Object[0]);

        synchronization = new MySynchronization(getSQLContext(), getFileManager(), getCredentials());
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

        Object[] array = synchronization.getRecords(attachments.Meta.table, "").toArray();
        for(Object o : array) {
            attachments attachment = (attachments)o;
            assertTrue(getFileManager().exists(FileManager.ATTACHMENTS, attachment.c_path));
        }
    }

    public static class MySynchronization extends ManualSynchronization {
        private final BasicCredentials mCredentials;
        public MySynchronization(WalkerSQLContext context, FileManager fileManager, BasicCredentials credentials) {
            super(context, fileManager, false);
            fileTid = UUID.randomUUID().toString();
            addEntity(new EntityAttachment(attachments.Meta.table, true, true).setParam(getUserID(), "1000.0.0.0").setUseCFunction().setTid(fileTid));
            //addEntity(new EntityAttachment(AttachmentsDao.TABLENAME, true, true).setParam(getUserID(), "1000.0.0.0").setUseCFunction().setTid(fileTid));
            mCredentials = credentials;
        }

        @Override
        protected Object sendBytes(String tid, byte[] bytes) {
            super.sendBytes(tid, bytes);

            try {
                MultipartUtility multipartUtility = new MultipartUtility(getBaseUrl() + "/synchronization/" + PreferencesManager.SYNC_PROTOCOL_v2, mCredentials);
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