package com.mobwal.android.library.authorization;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import com.mobwal.android.library.data.Meta;

@RunWith(AndroidJUnit4.class)
public class AuthorizationRequestTest {
    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void SuccessReadTest() {
        String successResult = "{\"token\": \"cm9vdDpyb290MA==\",\"user\": {\"id\": 1,\"claims\": \".master.admin.filer.\", \"login\":\"\"}, \"ip\": \"127.0.0.1\"}";
        AuthorizationRequest util = new AuthorizationRequest("");
        AuthorizationMeta meta = util.convertResponseToMeta(appContext, successResult, Meta.OK);
        assertEquals("cm9vdDpyb290MA==", meta.getToken());
        assertEquals((long)1, meta.getUserId().longValue());
        assertEquals(".master.admin.filer.", meta.getClaims());
        assertEquals(200, meta.getStatus());
        assertTrue(meta.isSuccess());
    }

    @Test
    public void FailReadTest() {
        String failResult = "{\"code\": 401,\"meta\": {\"success\": false,\"msg\": \"Пользователь не авторизован.\"}}";

        AuthorizationRequest util = new AuthorizationRequest("");
        AuthorizationMeta meta = util.convertResponseToMeta(appContext, failResult, Meta.NOT_AUTHORIZATION);

        assertFalse(meta.getMessage().isEmpty());
        assertEquals(401, meta.getStatus());
        assertFalse(meta.isSuccess());
    }
}