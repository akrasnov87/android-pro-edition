package com.mobwal.android.library.util;

import org.junit.Assert;
import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

import static org.junit.Assert.assertEquals;

import com.mobwal.android.library.util.DateUtil;

public class DateUtilTest {

    @Test
    public void convertStringToSystemDate() {
        Date dt = DateUtil.convertStringToSystemDate("2009-05-12T12:30:50Z");
        assert dt != null;
        assertEquals(dt.getTime(), Long.parseLong("1242117050000"));
    }

    @Test
    public void convertDateToSystemString() {
        Date dt = new Date(Long.parseLong("1242117050000"));
        String str = DateUtil.convertDateToSystemString(dt);
        assertEquals("2009-05-12T12:30:50Z", str);
    }

    @Test
    public void generateTid() {
        int tid = DateUtil.generateTid();
        Assert.assertTrue(tid > 0);
    }
}
