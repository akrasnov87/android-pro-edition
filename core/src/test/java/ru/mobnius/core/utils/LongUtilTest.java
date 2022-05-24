package ru.mobnius.core.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class LongUtilTest {

    @Test
    public void convertToLong() {
        assertEquals(LongUtil.convertToLong("123"), 123);
    }
}