package com.mobwal.android.library.util;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.File;

@RunWith(AndroidJUnit4.class)
public class LogUtilTest {

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LogUtilSingleton.createInstance(appContext);
        LogUtilSingleton.getInstance().clear(false);
    }

    @Test
    public void write() {
        for(int i = 0; i < 1000; i++) {
            LogUtilSingleton.getInstance().writeText(i + ": " + StringUtil.getRandomString(1024));
        }

        File file = LogUtilSingleton.getInstance().getArchiveLog(true);
        Assert.assertNotNull(file);
    }

    @After
    public void tearDown() {
        LogUtilSingleton.getInstance().clear(false);
    }
}