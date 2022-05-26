package com.mobwal.pro.utilits;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Random;

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