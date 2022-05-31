package com.mobwal.pro.data.manager.synchronization;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.UUID;

import ru.mobnius.core.data.DbOperationType;
import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.utils.LongUtil;
import ru.mobnius.core.utils.PackageReadUtils;
import ru.mobnius.core.utils.StringUtil;

import com.mobwal.pro.ManualSynchronization;
import com.mobwal.pro.WalkerSQLContext;
import com.mobwal.pro.data.DbGenerate;
import com.mobwal.pro.data.MultipartUtility;
import com.mobwal.pro.models.db.attachments;
import com.mobwal.pro.models.db.cd_results;
import com.mobwal.pro.utilits.SyncUtil;

import static org.junit.Assert.assertEquals;

public class ManualSynchronizationTest extends DbGenerate {
    private ManualSynchronizationTest.MySynchronization synchronization;

    private String mResultId;
    private String mPointId;

    @Before
    public void setUp() {

        cd_results point = new cd_results();
        getSQLContext().insert(point);

        synchronization = new MySynchronization(getSQLContext(), getFileManager(), getCredentials());
        synchronization.initEntities();

        // без этого фильтрации не будет работать
        // TODO 09/01/2020 нужно добавить проверку в метод start у синхронизации на передачу идентификатора пользователя, что null не было
        synchronization.getEntity(attachments.Meta.table).setSchema("dbo").setParam("null", "1000.0.0.0");
        //synchronization.getEntity(getDaoSession().getFilesDao().getTablename()).setParam("null", "1000.0.0.0");
    }

    @After
    public void tearDown() {
        getSQLContext().trash();
    }

    @Test
    public void from() throws IOException {
        byte[] bytes = synchronization.generatePackage(synchronization.dictionaryTid, (Object) null);
        byte[] results = (byte[]) synchronization.sendBytes(synchronization.dictionaryTid, bytes);
        PackageReadUtils utils = new PackageReadUtils(results, synchronization.isZip());
        synchronization.onProcessingPackage(utils, synchronization.dictionaryTid);
        int length = synchronization.getRecords(cd_results.Meta.table, "").toArray().length;
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

        boolean updateTid = SyncUtil.updateTid(synchronization, attachments.Meta.table, synchronization.fileTid);
        Assert.assertTrue(updateTid);

        //updateTid = SyncUtil.updateTid(synchronization, session.getFilesDao().getTablename(), synchronization.fileTid);
        //Assert.assertTrue(updateTid);

        byte[] bytes = synchronization.generatePackage(synchronization.fileTid, (Object) null);
        byte[] results = (byte[]) synchronization.sendBytes(synchronization.fileTid, bytes);

        try {
            fileManager.deleteFolder(FileManager.FILES);
        }catch (FileNotFoundException ignored) {

        }

        getSQLContext().exec("delete from " + attachments.Meta.table, new Object[0]);
        //getDaoSession().getAttachmentsDao().deleteAll();
        //getDaoSession().getFilesDao().deleteAll();

        PackageReadUtils utils = new PackageReadUtils(results, synchronization.isZip());
        synchronization.onProcessingPackage(utils, synchronization.fileTid);
        byte[] fileBytes = fileManager.readPath(FileManager.FILES, "file0.tmp");
        Assert.assertNotNull(fileBytes);
        assertEquals(new String(fileBytes), "file number 0");

        @SuppressWarnings("rawtypes") Collection records = synchronization.getRecords(attachments.Meta.table, "");
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
    public attachments saveFile(String c_name, byte[] bytes, String folder) throws IOException {
        FileManager fileManager = FileManager.getInstance();
        fileManager.writeBytes(folder, c_name, bytes);

        attachments file = new attachments();
        file.c_path = c_name;
        file.c_mime = StringUtil.getMimeByName(c_name);
        file.c_extension = StringUtil.getExtension(c_name);
        file.d_date = new Date();
        //file.folder = folder;
        file.__OBJECT_OPERATION_TYPE = DbOperationType.CREATED;

        getSQLContext().insert(file);
        return file;
    }

    public static class MySynchronization extends ManualSynchronization {
        private final BasicCredentials mCredentials;
        public MySynchronization(WalkerSQLContext context, FileManager fileManager, BasicCredentials credentials) {
            super(context, fileManager, false);
            dictionaryTid = UUID.randomUUID().toString();
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