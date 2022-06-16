package com.mobwal.pro;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import com.mobwal.android.library.SimpleFileManager;
import com.mobwal.android.library.authorization.credential.BasicCredential;

import com.mobwal.android.library.data.DbOperationType;
import com.mobwal.android.library.data.sync.MultipartUtility;
import com.mobwal.android.library.util.PackageReadUtils;

import com.mobwal.android.library.util.ReflectionUtil;
import com.mobwal.pro.models.db.Attachment;
import com.mobwal.android.library.util.SyncUtil;
import com.mobwal.pro.models.db.Audit;

import static org.junit.Assert.assertEquals;

public class ManualSynchronizationTest extends DbGenerate {
    private ManualSynchronizationTest.MySynchronization synchronization;

    @Before
    public void setUp() {
        Audit item = new Audit();
        item.c_session_id = UUID.randomUUID().toString();

        getSQLContext().insert(item);

        synchronization = new MySynchronization(getSQLContext(), getFileManager(), DbGenerate.getCredentials());
        synchronization.initEntities();
    }

    @After
    public void tearDown() {
        destroy();
    }

    @Test
    public void from() throws IOException {
        byte[] bytes = synchronization.generatePackage(synchronization.fileTid, (Object) null);
        byte[] results = (byte[]) synchronization.sendBytes(synchronization.fileTid, bytes);
        PackageReadUtils utils = new PackageReadUtils(results, synchronization.isZip());
        synchronization.onProcessingPackage(utils, synchronization.fileTid);
        int length = synchronization.getRecords(ReflectionUtil.getTableName(Audit.class), "").toArray().length;
        assertEquals(1, length);
        synchronization.destroy();
        utils.destroy();
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