package com.mobwal.pro;

import com.mobwal.android.library.SimpleFileManager;
import com.mobwal.android.library.data.DbOperationType;
import com.mobwal.android.library.data.sync.EntityAttachment;
import com.mobwal.android.library.data.sync.FileTransferWebSocketSynchronization;
import com.mobwal.android.library.data.sync.MultipartUtility;
import com.mobwal.android.library.util.ReflectionUtil;
import com.mobwal.android.library.data.sync.util.FullServerSidePackage;
import com.mobwal.pro.models.db.Attachment;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import com.mobwal.android.library.authorization.credential.BasicCredential;
import com.mobwal.android.library.data.rpc.FilterItem;
import com.mobwal.android.library.util.PackageReadUtils;

import com.mobwal.android.library.util.SyncUtil;

public class DeleteSynchronizationTest extends DbGenerate {
    private DeleteSynchronizationTest.MySynchronization synchronization;
    private String fileId = "";

    @Before
    public void setUp() {
        getSQLContext().exec("DELETE FROM " + ReflectionUtil.getTableName(Attachment.class), new Object[0]);
        fileId = UUID.randomUUID().toString();
        synchronization = new DeleteSynchronizationTest.MySynchronization(getSQLContext(), getFileManager(), DbGenerate.getCredentials(), DbGenerate.getBaseUrl());
        synchronization.initEntities();
    }

    @After
    public void tearDown() {
        getSQLContext().trash();
    }

    @Test
    public void deleteFile() throws IOException {
        Attachment file = new Attachment();
        file.id = fileId;
        file.c_path = "readme.txt";

        byte[] fileBytes = "Hello World!!!".getBytes();

        SimpleFileManager fileManager = synchronization.getFileManager();
        fileManager.writeBytes(file.c_path, fileBytes);

        file.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;

        getSQLContext().insert(file);
        SyncUtil.updateTid(synchronization, ReflectionUtil.getTableName(Attachment.class), synchronization.fileTid);

        byte[] bytes = synchronization.generatePackage(synchronization.fileTid, (Object) null);
        byte[] results = (byte[]) synchronization.sendBytes(synchronization.fileTid, bytes);
        PackageReadUtils utils = new PackageReadUtils(results, synchronization.isZip());
        synchronization.onProcessingPackage(utils, synchronization.fileTid);
        Object[] array = synchronization.getRecords(ReflectionUtil.getTableName(Attachment.class), "").toArray();

        Attachment resultFile = getFile(array, file.c_path);

        Assert.assertNotNull(resultFile);
        utils.destroy();

        removeFile(file.id);

        SyncUtil.updateTid(synchronization, ReflectionUtil.getTableName(Attachment.class), synchronization.fileTid);

        bytes = synchronization.generatePackage(synchronization.fileTid, (Object) null);
        results = (byte[]) synchronization.sendBytes(synchronization.fileTid, bytes);
        utils = new PackageReadUtils(results, synchronization.isZip());
        synchronization.onProcessingPackage(utils, synchronization.fileTid);
        array = synchronization.getRecords(ReflectionUtil.getTableName(Attachment.class), "").toArray();

        resultFile = getFile(array, file.c_path);
        Assert.assertNull(resultFile);
    }

    private Attachment getFile(Object[] array, String fileName) {
        Attachment resultFile = null;
        for (Object obj : array) {
            if (obj instanceof Attachment) {
                resultFile = (Attachment) obj;
                if (resultFile.c_path.equals(fileName)) {
                    break;
                } else {
                    resultFile = null;
                }
            }
        }
        return resultFile;
    }

    /**
     * Удаление файла по идентификтаору
     *
     * @param fileId идентификтаор
     */
    public void removeFile(String fileId) {
        Collection<Attachment> files = getSQLContext().select("select * from " + ReflectionUtil.getTableName(Attachment.class) + " where id = ?", new String[] { fileId }, Attachment.class);
        if (files != null) {
            Attachment file = files.iterator().next();
            if (file.__IS_SYNCHRONIZATION) {
                file.__OBJECT_OPERATION_TYPE = DbOperationType.REMOVED;
                file.__IS_DELETE = true;
                file.__IS_SYNCHRONIZATION = false;

                getSQLContext().insert(file);
            } else {
                getSQLContext().exec("delete from " + ReflectionUtil.getTableName(Attachment.class) + " where id = ?", new Object[] { fileId });
            }
            SimpleFileManager fileManager = new SimpleFileManager(getContext().getFilesDir(), getCredentials());
            fileManager.deleteFile(file.c_path);
        }
    }

    public class MySynchronization extends FileTransferWebSocketSynchronization {

        private final BasicCredential mCredentials;
        private final String mBaseUrl;

        public MySynchronization(WalkerSQLContext context, SimpleFileManager fileManager, BasicCredential credentials, String baseUrl) {
            super(context, "test", fileManager, false);
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
                MultipartUtility multipartUtility = new MultipartUtility(mBaseUrl + "/synchronization/v2", mCredentials);
                multipartUtility.addFilePart("synchronization", bytes);
                return multipartUtility.finish();
            } catch (Exception exc) {
                return null;
            }
        }

        @Override
        protected void initEntities() {
            fileTid = UUID.randomUUID().toString();
            addEntity(new EntityAttachment(Attachment.class)
                    .setSelect("id", "c_path", "fn_user", "fn_result", "fn_point", "fn_route", "n_longitude", "n_latitude", "d_date", "c_mime", "c_extension", "jb_data", "n_distance", "fn_storage")
                    .setFilter(new FilterItem("id", fileId))
                    .setClearable()
                    .setTid(fileTid)
                    .setParam(null, "null"));
        }
    }
}