package ru.mobnius.core.data.exception;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Date;
import java.util.Objects;

public class ExceptionModelTest {
    private Context mAppContext;

    @Before
    public void setUp() {
        mAppContext = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void toStringTest(){
        ExceptionModel model = ExceptionModel.getInstance(new Date(), "Ошибка", "NONE", 0);
        String str = model.toString();

        Assert.assertNotNull(str);
    }

    @Test
    public void toModelTest(){
        ExceptionModel model = ExceptionModel.getInstance(new Date(), "Ошибка", "NONE", 0);
        String str = model.toString();

        ExceptionModel model1 = ExceptionModel.toModel(str);
        Assert.assertEquals(model.getId(), Objects.requireNonNull(model1).getId());
        Assert.assertEquals(model.getDate().getTime() / 1000, model1.getDate().getTime() / 1000);
        Assert.assertEquals(model.getMessage(), model1.getMessage());
    }

    @Test
    public void getJSONDataTest() throws JSONException {
        ExceptionModel model = ExceptionModel.getInstance(new Date(), "Ошибка", "NONE", 0);
        String json = model.getJSONData(mAppContext);
        JSONObject jsonObject = new JSONObject(json);
        Assert.assertNotNull(jsonObject.getString("c_imei"));
        Assert.assertNotNull(jsonObject.getString("d_current_date"));
    }
}
