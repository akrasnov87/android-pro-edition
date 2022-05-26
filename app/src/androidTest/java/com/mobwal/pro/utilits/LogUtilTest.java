package com.mobwal.pro.utilits;

import static org.junit.Assert.*;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

public class LogUtilTest {
    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void write() throws IOException {
        for(int i = 0; i < 100; i++) {
            LogUtil.writeText(appContext, "Тест");
        }
    }

    @After
    public void tearDown() {

    }
}