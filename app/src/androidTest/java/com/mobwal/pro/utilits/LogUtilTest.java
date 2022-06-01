package com.mobwal.pro.utilits;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import com.mobwal.android.library.util.LogUtil;
import com.mobwal.android.library.util.StringUtil;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class LogUtilTest {
    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void write() {
        for(int i = 0; i < 1000; i++) {
            LogUtil.writeText(appContext, i + ": " + StringUtil.getRandomString(1024));
        }
    }

    @After
    public void tearDown() {

    }
}