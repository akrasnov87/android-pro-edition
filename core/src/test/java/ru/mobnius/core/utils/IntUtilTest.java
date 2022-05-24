package ru.mobnius.core.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class IntUtilTest {

    @Test
    public void convertToInt() {
        assertEquals(IntUtil.convertToInt("123"), 123);
    }
}