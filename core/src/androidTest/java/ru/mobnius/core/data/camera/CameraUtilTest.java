package ru.mobnius.core.data.camera;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import ru.mobnius.core.R;
import ru.mobnius.core.data.FileManager;
import ru.mobnius.core.data.configuration.DefaultPreferencesManager;
import ru.mobnius.core.data.configuration.PreferencesManager;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.utils.StreamUtil;

import static org.junit.Assert.assertNotNull;

public class CameraUtilTest {
    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        DefaultPreferencesManager.createInstance(appContext, "inspector");
        PreferencesManager.createInstance(appContext, "inspector");
    }

    @Test
    public void compress() {
        InputStream inStream = appContext.getResources().openRawResource(R.raw.pikachu);
        byte[] bytes = CameraUtil.compress(inStream, CameraUtil.JPEG_IMAGE_FORMAT, 60, "", "", "", appContext);
        assertNotNull(bytes);
    }
    @Test
    public void saveDataFromCamera() throws IOException {
        BasicCredentials credentials = new BasicCredentials("inspector", "");
        FileManager fileManager = FileManager.createInstance(credentials, appContext);
        InputStream inStream = appContext.getResources().openRawResource(R.raw.pikachu);
        CameraUtil.saveDataFromCamera(fileManager, "test.jpg", StreamUtil.readBytes(inStream));

        assertNotNull(fileManager.readPath(FileManager.PHOTOS, "test.jpg"));

        fileManager.clearUserFolder();
    }
}