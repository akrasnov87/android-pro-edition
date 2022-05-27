/*package ru.mobnius.cic.data.manager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.UUID;

import ru.mobnius.cic.data.storage.models.Points;
import ru.mobnius.cic.data.storage.models.Results;
import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.ui.image.ImageItem;
import ru.mobnius.core.utils.LocationUtil;
import ru.mobnius.core.utils.StreamUtil;
import ru.mobnius.cic.ManagerGenerate;
import ru.mobnius.cic.R;
import ru.mobnius.cic.data.storage.models.AttachmentTypes;
import ru.mobnius.cic.ui.model.Image;

import static org.junit.Assert.*;

public class PhotoManagerTest extends ManagerGenerate {
    private byte[] mBytes;

    private String mResultId;
    private PhotoManager mPhotoManager;

    @Before
    public void setUp() throws Exception {

        InputStream inStream = getContext().getResources().openRawResource(R.raw.preview);
        mBytes = StreamUtil.readBytes(inStream);

        AttachmentTypes attachmentType = new AttachmentTypes();
        attachmentType.id = (long)1;
        attachmentType.c_name = "Тип";
        getDaoSession().getAttachmentTypesDao().insert(attachmentType);

        attachmentType = new AttachmentTypes();
        attachmentType.id = (long)2;
        attachmentType.c_name = "Тип 2";
        getDaoSession().getAttachmentTypesDao().insert(attachmentType);

        Points point = new Points();
        String pointId;
        point.id = pointId = UUID.randomUUID().toString();
        getDaoSession().getPointsDao().insert(point);

        Results result = new Results();
        result.fn_point = pointId;
        result.id = mResultId = UUID.randomUUID().toString();
        getDaoSession().getResultsDao().insert(result);

        mPhotoManager = new PhotoManager(getContext());
    }

    @After
    public void tearDown() {
        getDaoSession().getAttachmentsDao().deleteAll();
        getDaoSession().getFilesDao().deleteAll();
        getDaoSession().getAttachmentTypesDao().deleteAll();
        getDaoSession().getResultsDao().deleteAll();
        getDaoSession().getPointsDao().deleteAll();

        mPhotoManager.destroy(getFileManager());
        getFileManager().clearUserFolder();
    }

    @Test
    public void addTempPicture() throws Exception {
        generateData();

        assertEquals(mPhotoManager.getImages().length, 3);

        Image three = Image.getInstance("second.png", 1, mResultId, "", LocationUtil.getLocation(0,0));
        try {
            mPhotoManager.addTempPicture(three);
            fail();
        }catch (Exception e) {
            assertEquals(mPhotoManager.getImages().length, 3);
        }
    }

    @Test
    public void updatePicture() throws Exception {
        generateData();

        ImageItem image = mPhotoManager.findImage("first.png");
        mPhotoManager.updatePicture(getDataManager(), image, 2, "my");
        image = mPhotoManager.findImage("first.png");
        assertEquals(image.getNotice(), "my");
        assertEquals(image.getType(), 2);

        image = mPhotoManager.findImage("three.png");
        mPhotoManager.updatePicture(getDataManager(), image, 2, "my");
        image = mPhotoManager.findImage("three.png");
        assertEquals(image.getNotice(), "my");
        assertEquals(image.getType(), 2);
    }

    @Test
    public void deletePicture() throws Exception {
        generateData();

        ImageItem image = mPhotoManager.findImage("first.png");
        mPhotoManager.deletePicture(getDataManager(), getFileManager(), image);
        image = mPhotoManager.findImage("first.png");
        assertNull(image);
        assertEquals(mPhotoManager.getImages().length, 2);

        image = mPhotoManager.findImage("three.png");
        mPhotoManager.deletePicture(getDataManager(), getFileManager(), image);
        image = mPhotoManager.findImage("three.png");
        assertNull(image);
        assertEquals(mPhotoManager.getImages().length, 1);
    }

    @Test
    public void savePictures() throws Exception {
        generateData();

        mPhotoManager.savePictures(getDataManager(), getFileManager(), UUID.randomUUID().toString(), mResultId);
        ImageItem image = mPhotoManager.findImage("first.png");
        assertNotNull(image);
        assertFalse(mPhotoManager.isTempImage(image));
        assertNotNull(getDataManager().getAttachment(image.getId()));

        image = mPhotoManager.findImage("second.png");
        assertNotNull(image);
        assertFalse(mPhotoManager.isTempImage(image));
        assertNotNull(getDataManager().getAttachment(image.getId()));

        assertEquals(mPhotoManager.getImages().length, 3);
    }

    @Test
    public void findImage() throws Exception {
        generateData();

        ImageItem image = mPhotoManager.findImage("second.png");
        assertNotNull(image);

        assertEquals(image.getNotice(), "my");
        assertEquals(image.getType(), 1);
        assertEquals(image.getName(), "second.png");
    }

    @Test
    public void isTempImage() throws Exception {
        generateData();

        ImageItem image = mPhotoManager.findImage("first.png");

        assertTrue(mPhotoManager.isTempImage(image));

        image = mPhotoManager.findImage("three.png");
        assertFalse(mPhotoManager.isTempImage(image));
    }

    private void generateData() throws Exception {
        Image first = Image.getInstance("first.png", 1, mResultId, "", LocationUtil.getLocation(0,0));
        getFileManager().writeBytes(FileManager.PHOTOS, first.getName(), mBytes);
        Image second = Image.getInstance("second.png", 1, mResultId, "my", LocationUtil.getLocation(0,0));
        getFileManager().writeBytes(FileManager.PHOTOS, second.getName(), mBytes);
        Image three = Image.getInstance("three.png", 1, mResultId, "", LocationUtil.getLocation(0,0));
        getFileManager().writeBytes(FileManager.PHOTOS, three.getName(), mBytes);

        getDataManager().saveAttachment(three.getName(), three.getType(), three.getResultId(), "", UUID.randomUUID().toString(), three.getNotice(), three.getLocation(), three.getBytes());
        Image[] images = new Image[1];
        images[0] = three;

        mPhotoManager.addPictures(images);

        mPhotoManager.addTempPicture(first);
        mPhotoManager.addTempPicture(second);
    }
}*/