package ru.mobnius.core.utils;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class NetworkInfoUtilTest {

    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void isNetworkAvailable() {
        assertTrue(NetworkInfoUtil.isNetworkAvailable(appContext));
    }
}