package com.mobwal.android.library.exception;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class FaceExceptionTest {
    @Test
    public void toStringTest(){
        FaceException model = new FaceException(new Date(), "Ошибка", "NONE", 0);
        String str = model.toString();

        Assert.assertNotNull(str);
    }

    @Test
    public void toModelTest(){
        FaceException model = new FaceException(new Date(), "Ошибка", "NONE", 0);
        String str = model.toString();

        FaceException model1 = FaceException.toFace(str);
        Assert.assertEquals(model.id, Objects.requireNonNull(model1).id);
        Assert.assertEquals(model.date.getTime() / 1000, model1.date.getTime() / 1000);
        Assert.assertEquals(model.message, model1.message);
    }
}
