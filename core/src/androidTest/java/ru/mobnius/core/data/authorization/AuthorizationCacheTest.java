package ru.mobnius.core.data.authorization;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;
import java.util.Date;

import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.credentials.BasicUser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class AuthorizationCacheTest {

    private AuthorizationCache cache;

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        cache = new AuthorizationCache(appContext);
    }

    @Test
    public void WriteTest() {
        BasicCredentials credentials = new BasicCredentials(GlobalSettings.DEFAULT_USER_NAME, GlobalSettings.DEFAULT_USER_PASSWORD);
        BasicUser user = new BasicUser(credentials, 1, "");
        cache.clear(true);
        assertTrue(cache.write(user));
        assertEquals(GlobalSettings.DEFAULT_USER_NAME, cache.read(GlobalSettings.DEFAULT_USER_NAME).getCredentials().login);
        assertEquals(1, cache.getNames().length);
        assertEquals(GlobalSettings.DEFAULT_USER_NAME, cache.getNames()[0]);
        boolean b = cache.clear(true);
        assertTrue(b);
        assertNull(cache.read(GlobalSettings.DEFAULT_USER_NAME));
        assertEquals(0, cache.getNames().length);
    }

    @Test
    public void updateTest() throws ParseException {
        BasicCredentials credentials = new BasicCredentials(GlobalSettings.DEFAULT_USER_NAME, GlobalSettings.DEFAULT_USER_PASSWORD);
        BasicUser user = new BasicUser(credentials, 1, "");
        cache.clear(true);
        assertTrue(cache.write(user));

        cache.update(GlobalSettings.DEFAULT_USER_NAME, "0000", new Date());
        assertNotNull(cache.readDate(GlobalSettings.DEFAULT_USER_NAME));
    }

    @After
    public void downUp() {
        cache.clear(true);
    }
}
