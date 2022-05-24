package ru.mobnius.core.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class SyncUtilTest {

    @Test
    public void getResetTidParams() {
        Object[] params = SyncUtil.getResetTidParams();
        assertEquals(params.length, 2);
        assertNull(params[0]);
        assertNull(params[1]);
    }
}