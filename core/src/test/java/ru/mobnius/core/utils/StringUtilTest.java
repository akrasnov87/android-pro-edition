package ru.mobnius.core.utils;

import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class StringUtilTest {

    @Test
    public void normalString() {
        assertEquals(StringUtil.normalString("null"), "");
        assertEquals(StringUtil.normalString("123"), "123");
    }

    @Test
    public void getSize() {
        String kb = StringUtil.getSize(5 * 1024);
        assertEquals(kb, "5,00 КБ");
        String mb = StringUtil.getSize(5 * 1024 * 1024);
        assertEquals(mb, "5,00 МБ");
        String gb = StringUtil.getSize((long)5 * (long)1024 * (long)1024 * (long)1024);
        assertEquals(gb, "5,00 ГБ");
        String tb = StringUtil.getSize((long)5 * (long)1024 * (long)1024 * (long)1024 * (long)1024);
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
    public void fullSpace() {
        assertEquals(StringUtil.fullSpace(5, "."), ".....");
        assertEquals(StringUtil.fullSpace(5, ""), "");
        assertEquals(StringUtil.fullSpace(-1, "."), "");
    }

    @Test
    public void md5() {
        assertNotNull(StringUtil.md5("test"));
    }

    @Test
    public void getMimeByName() {
        assertEquals(StringUtil.getMimeByName("image.jpg"), "image/jpeg");
        assertEquals(StringUtil.getMimeByName("image.png"), "image/png");
        assertEquals(StringUtil.getMimeByName("image.webp"), "image/webp");
        assertEquals(StringUtil.getMimeByName("audio.mp3"), "audio/mpeg");
        assertEquals(StringUtil.getMimeByName("video.mp4"), "video/mp4");
        assertEquals(StringUtil.getMimeByName("image.jpeg"), "application/octet-stream");
    }

    @Test
    public void getExtension() {
        String value1 = StringUtil.getExtension("image.jpg");
        assertEquals(value1, ".jpg");
        String value2 = StringUtil.getExtension("image.jpg.png");
        assertEquals(value2, ".png");
        String value3 = StringUtil.getExtension(".jpg");
        assertEquals(value3, ".jpg");
        String value4 = StringUtil.getExtension("jpg");
        assertNull(value4);
        String value5 = StringUtil.getExtension("");
        assertNull(value5);

        String value7 = StringUtil.getExtension("image.");
        assertNull(value7);
    }

    @Test
    public void exceptionToString() {
        Exception exception = new Exception("my test exception");
        String value = StringUtil.exceptionToString(exception);
        assertTrue(value.contains("java.lang.Exception: my test exception"));
    }
}
