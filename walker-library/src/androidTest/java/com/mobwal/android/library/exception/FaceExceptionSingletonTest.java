package com.mobwal.android.library.exception;

import android.content.res.Configuration;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class FaceExceptionSingletonTest {

    private ExceptionHandler mFaceExceptionSingleton;

    @Before
    public void setUp() {
        mFaceExceptionSingleton = new ExceptionHandler(InstrumentationRegistry.getInstrumentation().getTargetContext());
        mFaceExceptionSingleton.clearAll();
    }

    @After
    public void tearDown() {
        mFaceExceptionSingleton.clearAll();
    }

    @Test
    public void writeExceptionTest() {
        MaterialException model = new MaterialException(new Date(), "Ошибка", "NONE", 0, Configuration.ORIENTATION_PORTRAIT);
        String str = model.toString();
        String fileName = model.getFileName();
        mFaceExceptionSingleton.writeBytes(fileName, str.getBytes());
        Assert.assertTrue(mFaceExceptionSingleton.exists(fileName));
        byte[] bytes = mFaceExceptionSingleton.readPath(fileName);
        Assert.assertNotNull(bytes);
        String result = new String(bytes);
        MaterialException exceptionModel = MaterialException.toFace(result);
        Assert.assertEquals(Objects.requireNonNull(exceptionModel).message, model.message);
        mFaceExceptionSingleton.deleteFile(fileName);
        Assert.assertFalse(mFaceExceptionSingleton.exists(fileName));
    }

    @Test
    public void getExceptionTest() {
        String exceptionID = "";
        for(int i = 0; i < 2; i++){
            long time = new Date().getTime();
            MaterialException model = new MaterialException(new Date(time + (1000 * 60 * (i + 1))), "Ошибка #" + i, "NONE", 0, Configuration.ORIENTATION_PORTRAIT);
            String str = model.toString();
            String fileName = model.getFileName();
            mFaceExceptionSingleton.writeBytes(fileName, str.getBytes());
            if(i == 1) {
                exceptionID = model.id;
            }
        }

        Assert.assertEquals(Objects.requireNonNull(mFaceExceptionSingleton.getExceptionList()).size(), 2);
        Assert.assertEquals(Objects.requireNonNull(mFaceExceptionSingleton.getException(exceptionID)).message, "Ошибка #1");
        Assert.assertEquals(Objects.requireNonNull(mFaceExceptionSingleton.getLastException()).message, "Ошибка #1");
    }
}
