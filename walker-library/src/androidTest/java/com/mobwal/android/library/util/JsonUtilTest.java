package com.mobwal.android.library.util;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Hashtable;
import java.util.Objects;

@RunWith(AndroidJUnit4.class)
public class JsonUtilTest {

    @Test
    public void toHashObject() {
        String jsonStr = "{\"id\": 1,\"c_name\":\"test\"}";

        Hashtable<String, Object> variables = JsonUtil.toHashObject(jsonStr);
        Assert.assertEquals(variables.size(), 2);
        String str = Objects.requireNonNull(variables.get("id")).toString();
        Assert.assertEquals(Integer.parseInt(str), 1);

        variables = new Hashtable<>();
        variables.put("id", 1);
        variables.put("c_name", "test");

        Assert.assertEquals(JsonUtil.toString(variables), "{\"c_name\":\"test\",\"id\":1}");
    }
}
