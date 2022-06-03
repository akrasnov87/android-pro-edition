package com.mobwal.android.library.data.sync;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.mobwal.android.library.data.sync.util.transfer.TransferData;
import com.mobwal.android.library.data.sync.util.transfer.TransferProgress;
import com.mobwal.android.library.data.sync.util.transfer.TransferSpeed;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TransferProgressTest {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void toStringTest() {
        TransferProgress transferProgress = new TransferProgress(70,
                new TransferSpeed(1024 * 1024, 10 * 1000),
                new TransferData(512, 1024),
                68 * 1000);
        String txt = transferProgress.toString(mContext);
        Assert.assertEquals(txt.replace(',', '.'), "~00:01:08(102.40 КБ\\сек.)");
    }
}