package com.mobwal.android.library.util;

import static org.junit.Assert.*;

import com.mobwal.android.library.util.StringUtil;

import org.junit.Assert;
import org.junit.Test;

import java.util.UUID;

public class StringUtilTest {
    @Test
    public void getShortGuid() {
        Assert.assertEquals(StringUtil.getShortGuid("44e5386d-d461-4693-b3a8-cef54a795336"), "44e5386d");
    }

    @Test
    public void normalString() {
        assertEquals(StringUtil.normalString("null"), "");
        assertEquals(StringUtil.normalString("123"), "123");
    }

    @Test
    public void getSize() {
        String kb = StringUtil.getSize(null,5 * 1024);
        assertEquals(kb, "5,00 КБ");
        String mb = StringUtil.getSize(null,5 * 1024 * 1024);
        assertEquals(mb, "5,00 МБ");
        String gb = StringUtil.getSize(null,(long)5 * (long)1024 * (long)1024 * (long)1024);
        assertEquals(gb, "5,00 ГБ");
        String tb = StringUtil.getSize(null,(long)5 * (long)1024 * (long)1024 * (long)1024 * (long)1024);
        assertEquals(tb, "5,00 ТБ");
    }

    @Test
    public void isEmptyOrNull() {
        assertTrue(StringUtil.isEmptyOrNull("null"));
        assertTrue(StringUtil.isEmptyOrNull(""));
        assertTrue(StringUtil.isEmptyOrNull(null));
        assertFalse(StringUtil.isEmptyOrNull("123"));
    }

    @Test
    public void getFileExtension() {
        String value1 = StringUtil.getFileExtension("image.jpg");
        assertEquals(value1, ".jpg");
        String value2 = StringUtil.getFileExtension("image.jpg.png");
        assertEquals(value2, ".png");
        String value3 = StringUtil.getFileExtension(".jpg");
        assertEquals(value3, ".jpg");
        String value4 = StringUtil.getFileExtension("jpg");
        assertEquals(value4, "");
        String value5 = StringUtil.getFileExtension("");
        assertEquals(value5, "");

        String value7 = StringUtil.getFileExtension("image.");
        assertNull(value7);
    }

    @Test
    public void exceptionToString() {
        Exception exception = new Exception("my test exception");
        String value = StringUtil.exceptionToString(exception);
        assertTrue(value.contains("java.lang.Exception: my test exception"));
    }
}