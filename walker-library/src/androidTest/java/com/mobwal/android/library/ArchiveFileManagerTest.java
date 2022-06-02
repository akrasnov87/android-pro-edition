package com.mobwal.android.library;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.mobwal.android.library.ArchiveFileManager;
import com.mobwal.android.library.SimpleFileManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class ArchiveFileManagerTest {

    private Context mContext;
    private String mFolder;
    private File tempFolder;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mFolder = "temp";
        tempFolder = new File(mContext.getCacheDir(), mFolder);
        SimpleFileManager.deleteRecursive(mContext, tempFolder);
    }

    @Test
    public void archive() throws IOException {
        SimpleFileManager simpleFileManager = new SimpleFileManager(mContext, new File(mContext.getCacheDir(), mFolder));
        simpleFileManager.writeBytes(mFolder, "readme.txt", "hello world!!!".getBytes(StandardCharsets.UTF_8));
        simpleFileManager.writeBytes(mFolder, "points.txt", "points".getBytes(StandardCharsets.UTF_8));

        File fileOutput = new File(mContext.getCacheDir(), mFolder + ".zip");
        String output = fileOutput.getPath();

        String result = ArchiveFileManager.zip(mContext, tempFolder, output);
        Assert.assertNull(result);
        SimpleFileManager.deleteRecursive(mContext, tempFolder);
        assertFalse(tempFolder.exists());

        Assert.assertTrue(fileOutput.exists());
        Assert.assertTrue(fileOutput.length() > 0);

        result = ArchiveFileManager.unzip(mContext, output, mContext.getCacheDir().getPath());
        Assert.assertNull(result);
        Assert.assertTrue(tempFolder.exists());

        Assert.assertEquals(Objects.requireNonNull(tempFolder.listFiles()).length, 2);
        SimpleFileManager.deleteRecursive(mContext, tempFolder);

        Assert.assertTrue(fileOutput.delete());
    }

    @After
    public void tearDown() {
        SimpleFileManager.deleteRecursive(mContext, tempFolder);
    }
}