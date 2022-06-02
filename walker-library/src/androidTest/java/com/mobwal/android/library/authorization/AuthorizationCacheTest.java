package com.mobwal.android.library.authorization;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.ParseException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.mobwal.android.library.authorization.credential.BasicCredential;
import com.mobwal.android.library.authorization.credential.BasicUser;

@RunWith(AndroidJUnit4.class)
public class AuthorizationCacheTest {

    private final String DEFAULT_USER_NAME = "user";
    private final String DEFAULT_USER_PASSWORD = "password";

    private AuthorizationCache cache;

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        cache = new AuthorizationCache(appContext);
    }

    @Test
    public void WriteTest() {
        BasicCredential credentials = new BasicCredential(DEFAULT_USER_NAME, DEFAULT_USER_PASSWORD);
        BasicUser user = new BasicUser(credentials, 1, "");
        cache.clear(null);
        assertTrue(cache.write(user));
        assertEquals(DEFAULT_USER_NAME, cache.read(DEFAULT_USER_NAME).getCredential().login);
        assertEquals(1, cache.getNames().length);
        assertEquals(DEFAULT_USER_NAME, cache.getNames()[0]);
        boolean b = cache.clear(null);
        assertTrue(b);
        assertNull(cache.read(DEFAULT_USER_NAME));
        assertEquals(0, cache.getNames().length);
    }

    @Test
    public void updateTest() {
        BasicCredential credentials = new BasicCredential(DEFAULT_USER_NAME, DEFAULT_USER_PASSWORD);
        BasicUser user = new BasicUser(credentials, 1, "");
        cache.clear(null);
        assertTrue(cache.write(user));

        assertNotNull(cache.readDate(DEFAULT_USER_NAME));
    }

    @After
    public void downUp() {
        cache.clear(null);
    }
}
