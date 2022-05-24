package ru.mobnius.core.data.authorization;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class AuthorizationRequestUtilTest {

    @Test
    public void SuccessReadTest() {
        String successResult = "{\"token\": \"cm9vdDpyb290MA==\",\"user\": {\"userId\": 1,\"claims\": \".master.admin.filer.\", \"userName\":\"\"}}";
        AuthorizationRequestUtil util = new AuthorizationRequestUtil();
        AuthorizationMeta meta = util.convertResponseToMeta(successResult);
        assertEquals("cm9vdDpyb290MA==", meta.getToken());
        assertEquals(1, meta.getUserId().intValue());
        assertEquals(".master.admin.filer.", meta.getClaims());
        assertEquals(200, meta.getStatus());
        assertTrue(meta.isSuccess());
    }

    @Test
    public void FailReadTest() {
        String failResult = "{\"code\": 401,\"meta\": {\"success\": false,\"msg\": \"Пользователь не авторизован.\"}}";

        AuthorizationRequestUtil util = new AuthorizationRequestUtil();
        AuthorizationMeta meta = util.convertResponseToMeta(failResult);

        assertFalse(meta.getMessage().isEmpty());
        assertEquals(401, meta.getStatus());
        assertFalse(meta.isSuccess());
    }
}