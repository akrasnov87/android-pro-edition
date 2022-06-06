package com.mobwal.pro;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import com.mobwal.android.library.FileManager;
import com.mobwal.android.library.authorization.credential.BasicCredential;

import com.mobwal.android.library.data.DbOperationType;
import com.mobwal.android.library.data.sync.MultipartUtility;
import com.mobwal.android.library.util.PackageReadUtils;

import com.mobwal.android.library.util.ReflectionUtil;
import com.mobwal.pro.models.db.Attachment;
import com.mobwal.pro.models.db.Result;
import com.mobwal.android.library.util.SyncUtil;

import static org.junit.Assert.assertEquals;

public class ManualSynchronizationTest extends DbGenerate {
    private ManualSynchronizationTest.MySynchronization synchronization;

    private String mResultId;
    private String mPointId;

    @Before
    public void setUp() {

        Result point = new Result();
        getSQLContext().insert(point);

        synchronization = new MySynchronization(getSQLContext(), getFileManager(), DbGenerate.getCredentials());
        synchronization.initEntities();

        // без этого фильтрации не будет работать
        // TODO 09/01/2020 нужно добавить проверку в метод start у синхронизации на передачу идентификатора пользователя, что null не было
        synchronization.getEntity(ReflectionUtil.getTableName(Attachment.class)).setSchema("dbo").setParam("1000.0.0.0");
        //synchronization.getEntity(getDaoSession().getFilesDao().getTablename()).setParam("null", "1000.0.0.0");
    }

    @After
    public void tearDown() {
        getSQLContext().trash();
    }

    @Test
    public void from() throws IOException {
        byte[] bytes = synchronization.generatePackage(synchronization.totalTid, (Object) null);
        byte[] results = (byte[]) synchronization.sendBytes(synchronization.totalTid, bytes);
        PackageReadUtils utils = new PackageReadUtils(results, synchronization.isZip());
        synchronization.onProcessingPackage(utils, synchronization.totalTid);
        int length = synchronization.getRecords(ReflectionUtil.getTableName(Attachment.class), "").toArray().length;
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

        for(int i = 0; i < 2; i++) {
            byte[] bytes = ("file number " + i).getBytes();
            saveFile("file" + i + ".tmp", bytes, FileManager.FILES);
        }

        boolean updateTid = SyncUtil.updateTid(synchronization, ReflectionUtil.getTableName(Attachment.class), synchronization.fileTid);
        Assert.assertTrue(updateTid);

        //updateTid = SyncUtil.updateTid(synchronization, session.getFilesDao().getTablename(), synchronization.fileTid);
        //Assert.assertTrue(updateTid);

        byte[] bytes = synchronization.generatePackage(synchronization.fileTid, (Object) null);
        byte[] results = (byte[]) synchronization.sendBytes(synchronization.fileTid, bytes);

        try {
            fileManager.deleteFolder(FileManager.FILES);
        }catch (FileNotFoundException ignored) {

        }

        getSQLContext().exec("delete from " + ReflectionUtil.getTableName(Attachment.class), new Object[0]);
        //getDaoSession().getAttachmentsDao().deleteAll();
        //getDaoSession().getFilesDao().deleteAll();

        PackageReadUtils utils = new PackageReadUtils(results, synchronization.isZip());
        synchronization.onProcessingPackage(utils, synchronization.fileTid);
        byte[] fileBytes = fileManager.readPath(FileManager.FILES, "file0.tmp");
        Assert.assertNull(fileBytes);

        @SuppressWarnings("rawtypes") Collection records = synchronization.getRecords(ReflectionUtil.getTableName(Attachment.class), "");
        Assert.assertTrue(records.size() >= 2);

        //records = synchronization.getRecords(session.getFilesDao().getTablename(), "");
        //Assert.assertTrue(records.size() >= 2);

        synchronization.destroy();
        try {
            fileManager.deleteFolder(FileManager.FILES);
        }catch (FileNotFoundException ignored){

        }
        utils.destroy();
    }

    /**
     * Сохранение файла
     *
     * @param c_name имя файла
     * @param bytes  массив байтов
     * @param folder каталог
     * @return файл
     * @throws IOException исключение
     */
    public Attachment saveFile(String c_name, byte[] bytes, String folder) throws IOException {
        FileManager fileManager = FileManager.getInstance();
        fileManager.writeBytes(folder, c_name, bytes);

        Attachment file = new Attachment();
        file.c_path = c_name;
        file.d_date = new Date();
        //file.folder = folder;
        file.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;

        getSQLContext().insert(file);
        return file;
    }

    public static class MySynchronization extends ManualSynchronization {
        private static long DEFAULT_USER_ID = 4;

        private final BasicCredential mCredentials;
        public MySynchronization(WalkerSQLContext context, FileManager fileManager, BasicCredential credentials) {
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
            return DEFAULT_USER_ID;
        }
    }
}