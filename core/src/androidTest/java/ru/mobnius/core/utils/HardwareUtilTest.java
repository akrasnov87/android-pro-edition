package ru.mobnius.core.utils;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;

public class HardwareUtilTest {

    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void getIMEI() {
        String id = HardwareUtil.getNumber(appContext);
        assertNotNull(id);
    }

    @Test
    public void getBatteryPercentage() {
        assertNotNull(HardwareUtil.getBatteryPercentage(appContext));
    }
}