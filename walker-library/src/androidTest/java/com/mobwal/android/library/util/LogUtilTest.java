package com.mobwal.android.library.util;

import android.content.Context;
import android.util.Log;

import androidx.test.platform.app.InstrumentationRegistry;

import com.mobwal.android.library.util.LogUtil;
import com.mobwal.android.library.util.StringUtil;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

public class LogUtilTest {
    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        LogUtil.clear(appContext);
    }

    @Test
    public void write() {
        for(int i = 0; i < 1000; i++) {
            LogUtil.writeText(appContext, i + ": " + StringUtil.getRandomString(1024));
        }

        File file = LogUtil.getArchiveLog(appContext);
        Assert.assertNotNull(file);
    }

    @After
    public void tearDown() {
        LogUtil.clear(appContext);
    }
}