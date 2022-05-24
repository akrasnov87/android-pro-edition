package ru.mobnius.core.utils;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;

import ru.mobnius.core.R;

import static org.junit.Assert.assertTrue;

public class StreamUtilTest {
    private Context appContext;

    @Before
    public void setUp() {
        appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
    }

    @Test
    public void readBytes() throws IOException {
        InputStream inStream = appContext.getResources().openRawResource(R.raw.pikachu);
        byte[] array = StreamUtil.readBytes(inStream);
        assertTrue(array.length > 0);
    }
}