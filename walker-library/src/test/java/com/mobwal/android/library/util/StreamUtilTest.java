package com.mobwal.android.library.util;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamUtilTest {

    @Test
    public void readBytes() throws IOException {
        InputStream inStream = new ByteArrayInputStream("Hello World".getBytes());
        byte[] array = StreamUtil.readBytes(inStream);
        assertEquals(11, array.length);
    }
}
