package ru.mobnius.core.data.synchronization.utils.transfer;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TransferDataTest {

    @Test
    public void toStringTest() {
        TransferData data = TransferData.getInstance(512, 1024);
        String txt = data.toString();
        Assert.assertEquals(txt.replace(',', '.'), "512 байт/1.00 КБ");
    }
}