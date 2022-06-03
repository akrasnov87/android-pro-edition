package com.mobwal.android.library.data.sync;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.mobwal.android.library.data.sync.util.transfer.TransferSpeed;

@RunWith(AndroidJUnit4.class)
public class TransferSpeedTest {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void toStringTest() {
        String txt = new TransferSpeed(1024 * 1024, 2 * 1000).toString(mContext);
        assertEquals(txt.replace(',', '.'), "512.00 КБ\\сек.");
    }
}