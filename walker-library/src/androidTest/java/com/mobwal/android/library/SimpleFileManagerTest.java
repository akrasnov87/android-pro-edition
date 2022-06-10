package com.mobwal.android.library;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.mobwal.android.library.SimpleFileManager;
import com.mobwal.android.library.authorization.credential.BasicCredential;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

@RunWith(AndroidJUnit4.class)
public class SimpleFileManagerTest {
    private SimpleFileManager fileManager;
    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        fileManager = new SimpleFileManager(appContext.getFilesDir(), new BasicCredential("user", "1234"));
    }

    @Test
    public void write() throws IOException {
        fileManager.writeBytes("pic.txt", "picture 1".getBytes());

        assertTrue(fileManager.exists("pic.txt"));

        fileManager.writeBytes("pic2.txt", "picture 2".getBytes());

        byte[] bytes = fileManager.readPath("pic.txt");
        Assert.assertEquals("picture 1", new String(bytes));

        fileManager.deleteFile("pic2.txt");
        assertNull(fileManager.readPath("pic2.txt"));

        fileManager.deleteFolder();
        assertNull(fileManager.readPath("pic.txt"));
    }

    @After
    public void tearDown() {
        SimpleFileManager.deleteRecursive(appContext.getFilesDir());
    }
}