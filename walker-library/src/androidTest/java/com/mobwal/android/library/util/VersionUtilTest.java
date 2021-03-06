package com.mobwal.android.library.util;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.mobwal.android.library.Version;
import com.mobwal.android.library.util.VersionUtil;

public class VersionUtilTest {
    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void getVersionName() {
        String versionName = VersionUtil.getVersionName(appContext);
        assertNotNull(versionName);
        Version version = new Version();
        assertEquals(version.getVersionParts(versionName).length, 4);
    }

    @Test
    public void getShortVersionName() {
        String versionName = VersionUtil.getShortVersionName(appContext);
        assertNotNull(versionName);
        Version version = new Version();
        assertNull(version.getVersionParts(versionName));
    }
}
