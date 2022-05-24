package ru.mobnius.core.data.authorization;

import android.content.Context;

import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.mobnius.core.data.GlobalSettings;
import ru.mobnius.core.data.credentials.BasicCredentials;
import ru.mobnius.core.data.credentials.BasicUser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class AuthorizationTest {
    private Authorization mAuthorization;

    @Before
    public void setUp() {
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        mAuthorization = Authorization.createInstance(appContext);
    }

    @After
    public void tearDown() {
        mAuthorization.destroy();
        assertFalse(mAuthorization.isAuthorized());
    }

    @Test
    public void isAutoSignIn() {
        mAuthorization.setUser(new BasicUser(new BasicCredentials(GlobalSettings.DEFAULT_USER_NAME, GlobalSettings.DEFAULT_USER_PASSWORD), 1, ".user.inspector."));
        assertTrue(mAuthorization.isAuthorized());
        assertTrue(mAuthorization.isAutoSignIn());
        mAuthorization.setUser(new BasicUser(new BasicCredentials("temp", "temp0"), 1, ".user.inspector."));
        assertFalse(mAuthorization.isAutoSignIn());
        assertNull(mAuthorization.getLastAuthUser());
        mAuthorization.reset();
        assertFalse(mAuthorization.isAuthorized());
    }

    @Test
    public void isInspector() {
        mAuthorization.setUser(new BasicUser(new BasicCredentials(GlobalSettings.DEFAULT_USER_NAME, GlobalSettings.DEFAULT_USER_PASSWORD), 1, ".user.inspector."));
        assertTrue(mAuthorization.isInspector());
        mAuthorization.setUser(new BasicUser(new BasicCredentials(GlobalSettings.DEFAULT_USER_NAME, GlobalSettings.DEFAULT_USER_PASSWORD), 1, ".user.admin."));
        assertFalse(mAuthorization.isInspector());
    }

    @Test
    public void getLastAuthUser() {
        mAuthorization.setUser(new BasicUser(new BasicCredentials(GlobalSettings.DEFAULT_USER_NAME, GlobalSettings.DEFAULT_USER_PASSWORD), 1, ".user.inspector."));
        BasicUser basicUser = mAuthorization.getLastAuthUser();
        assertEquals(basicUser.getCredentials().login, GlobalSettings.DEFAULT_USER_NAME);
    }
}