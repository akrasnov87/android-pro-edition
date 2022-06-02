package com.mobwal.android.library;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.mobwal.android.library.SimpleFileManager;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class SimpleFileManagerTest {
    private SimpleFileManager fileManager;
    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();

        fileManager = new SimpleFileManager(appContext, appContext.getFilesDir());
    }

    @Test
    public void write() throws IOException {
        fileManager.writeBytes("pictures", "pic.txt", "picture 1".getBytes());

        assertTrue(fileManager.exists("pictures", "pic.txt"));

        fileManager.writeBytes("pictures", "pic2.txt", "picture 2".getBytes());

        byte[] bytes = fileManager.readPath("pictures", "pic.txt");
        Assert.assertEquals("picture 1", new String(bytes));

        fileManager.deleteFile("pictures", "pic2.txt");
        assertNull(fileManager.readPath("pictures", "pic2.txt"));

        fileManager.deleteFolder("pictures");
        assertNull(fileManager.readPath("pictures", "pic.txt"));
    }

    @After
    public void tearDown() {
        SimpleFileManager.deleteRecursive(appContext, appContext.getFilesDir());
    }
}