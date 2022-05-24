package ru.mobnius.core.utils;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class BooleanUtilTest {

    @Test
    public void isBooleanValue() {
        assertTrue(BooleanUtil.isBooleanValue("true"));
        assertTrue(BooleanUtil.isBooleanValue("false"));
        assertFalse(BooleanUtil.isBooleanValue("0"));
    }

    @Test
    public void isBooleanValueTrue() {
        assertTrue(BooleanUtil.isBooleanValueTrue("true"));
        assertFalse(BooleanUtil.isBooleanValueTrue("0"));
    }

    @Test
    public void convertToBoolean() {
        assertTrue(BooleanUtil.convertToBoolean("true"));
        assertFalse(BooleanUtil.convertToBoolean("false"));
    }
}