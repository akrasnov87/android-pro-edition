package com.mobwal.android.library.data.sync;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.mobwal.android.library.data.sync.util.transfer.TransferData;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TransferDataTest {

    private Context mContext;

    @Before
    public void setUp() {
        mContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void toStringTest() {
        TransferData data = new TransferData(512, 1024);
        String txt = data.toString(mContext);
        Assert.assertEquals(txt.replace(',', '.'), "512 байт/1.00 КБ");
    }
}