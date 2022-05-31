package com.mobwal.pro.data.manager.synchronization;

import com.mobwal.pro.WalkerSQLContext;
import com.mobwal.pro.data.DbGenerate;
import com.mobwal.pro.data.EntityAttachment;
import com.mobwal.pro.data.FileTransferWebSocketSynchronization;
import com.mobwal.pro.data.MultipartUtility;
import com.mobwal.pro.data.utils.FullServerSidePackage;
import com.mobwal.pro.models.db.attachments;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.UUID;

import ru.mobnius.core.data.DbOperationType;
import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.rpc.FilterItem;
import ru.mobnius.core.utils.PackageReadUtils;

import com.mobwal.pro.utilits.SyncUtil;

public class DeleteSynchronizationTest extends DbGenerate {
    private DeleteSynchronizationTest.MySynchronization synchronization;
    private String fileId = "";

    @Before
    public void setUp() {
        getSQLContext().exec("DELETE FROM " + attachments.Meta.table, new Object[0]);
        fileId = UUID.randomUUID().toString();
        synchronization = new DeleteSynchronizationTest.MySynchronization(getSQLContext(), getFileManager(), getCredentials(), getBaseUrl());
        synchronization.initEntities();
    }

    @After
    public void tearDown() {
        getSQLContext().trash();
    }

    @Test
    public void deleteFile() throws IOException {
        attachments file = new attachments();
        file.id = fileId;
        file.c_path = "readme.txt";

        byte[] fileBytes = "Hello World!!!".getBytes();

        FileManager fileManager = synchronization.getFileManager();
        fileManager.writeBytes(FileManager.FILES, file.c_path, fileBytes);

        file.c_mime = "text/plain";
        file.c_extension = ".txt";
        file.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;

        getSQLContext().insert(file);
        SyncUtil.updateTid(synchronization, attachments.Meta.table, synchronization.fileTid);

        byte[] bytes = synchronization.generatePackage(synchronization.fileTid, (Object) null);
        byte[] results = (byte[]) synchronization.sendBytes(synchronization.fileTid, bytes);
        PackageReadUtils utils = new PackageReadUtils(results, synchronization.isZip());
        synchronization.onProcessingPackage(utils, synchronization.fileTid);
        Object[] array = synchronization.getRecords(attachments.Meta.table, "").toArray();

        attachments resultFile = getFile(array, file.c_path);

        Assert.assertNotNull(resultFile);
        utils.destroy();

        removeFile(file.id);

        SyncUtil.updateTid(synchronization, attachments.Meta.table, synchronization.fileTid);

        bytes = synchronization.generatePackage(synchronization.fileTid, (Object) null);
        results = (byte[]) synchronization.sendBytes(synchronization.fileTid, bytes);
        utils = new PackageReadUtils(results, synchronization.isZip());
        synchronization.onProcessingPackage(utils, synchronization.fileTid);
        array = synchronization.getRecords(attachments.Meta.table, "").toArray();

        resultFile = getFile(array, file.c_path);
        Assert.assertNull(resultFile);
    }

    private attachments getFile(Object[] array, String fileName) {
        attachments resultFile = null;
        for (Object obj : array) {
            if (obj instanceof attachments) {
                resultFile = (attachments) obj;
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
        Collection<attachments> files = getSQLContext().select("select * from " + attachments.Meta.table + " where id = ?", new String[] { fileId }, attachments.class);
        if (files != null) {
            attachments file = files.iterator().next();
            if (file.__IS_SYNCHRONIZATION) {
                file.__OBJECT_OPERATION_TYPE = DbOperationType.REMOVED;
                file.__IS_DELETE = true;
                file.__IS_SYNCHRONIZATION = false;

                getSQLContext().insert(file);
                //daoSession.getFilesDao().update(files);
            } else {
                getSQLContext().exec("delete from " + attachments.Meta.table + " where id = ?", new Object[] { fileId });
            }
            FileManager fileManager = FileManager.getInstance();
            try {
                fileManager.deleteFile(FileManager.FILES, file.c_path);
            } catch (FileNotFoundException ignore) {
                // это нормально
            }
        }
    }

    public class MySynchronization extends FileTransferWebSocketSynchronization {

        private final BasicCredentials mCredentials;
        private final String mBaseUrl;

        public MySynchronization(WalkerSQLContext context, FileManager fileManager, BasicCredentials credentials, String baseUrl) {
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
            addEntity(new EntityAttachment(attachments.Meta.table, true, true)
                    .setSelect("id", "c_path", "fn_user", "fn_result", "fn_point", "fn_route", "n_longitude", "n_latitude", "d_date", "c_mime", "c_extension", "jb_data", "n_distance", "fn_storage")
                    .setFilter(new FilterItem("id", fileId))
                    .setTid(fileTid)
                    .setParam(null, "null"));
        }
    }
}